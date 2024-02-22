import Button from './Button';
import { ReservationData, ReservationStatus, reservationStatus } from '@/fetches/reservations/Reservation.type';
import { server } from '@/fetches/common/axios';
import { Link } from 'react-router-dom';
import type { ResponseWithoutData } from '@/fetches/common/response.type';
import { useEffect, useState } from 'react';
import type { MouseEventHandler } from 'react';
import { distance } from '@/utils/DistanceCalculater';
import { dateTimeFormatter } from '@/utils/converters';
import StarIcon from './review/StarIcon';

type ButtonType = 'disabled' | 'enabled' | 'danger';

export type TargetType = 'host' | 'guest';

type Props = {
  type: TargetType;
  reservation: ReservationData;
  className?: string;
  reviewOnClick: MouseEventHandler<HTMLButtonElement>;
  isReviewed: boolean
};

function ListComponent({ type, reservation, className, reviewOnClick, isReviewed }: Props) {
  const [buttonText, setButtonText] = useState('');
  const [buttonType, setButtonType] = useState<ButtonType>('disabled');
  const [buttonClick, setButtonClick] = useState<((reservation: ReservationData) => void) | null>(null);
  const [rentStatus, setRentStatus] = useState(reservation.rentStatus);
  const [timeDifference, setTimeDifference] = useState(0);
  const [extraFee, setExtraFee] = useState(0);

  useEffect(() => {
    const updateTimeDifference = () => {
      const returnTime = new Date(Number(reservation.rentPeriod[1]));
      // 현재 시간과 반납 시간의 차이 계산 전, 숫자 타입 확인
      const now = new Date().getTime(); // getTime()은 항상 숫자를 반환
      const returnTimeMs = returnTime.getTime(); // 반납 시간을 밀리초로 변환

      // 이제 returnTimeMs와 now는 숫자 타입이므로 산술 연산 가능
      const difference = returnTimeMs - now;

      if (difference < 0) {
        const hours = Math.floor(difference / (1000 * 60 * 60));
        setTimeDifference(hours * -1);
      }
    };

    // 600초마다 시간 차이 업데이트
    const interval = setInterval(updateTimeDifference, 30000);

    // 초기 로드 시에도 바로 업데이트
    updateTimeDifference();

    // 컴포넌트 언마운트 시 인터벌 정리
    return () => clearInterval(interval);
  }, [reservation.rentPeriod]);

  function calculateExtraFee(overTime: number) {
    return overTime * reservation.rentFee;
  }

  useEffect(() => {
    if (timeDifference > 0) {
      const newExtraFee = calculateExtraFee(timeDifference);
      setExtraFee(newExtraFee);
    } else {
      setExtraFee(0);
    }
  }, [timeDifference]);

  useEffect(() => {
    // reservation.status 값에 따라 Button 상태 업데이트
    const statusConfig = buttonByReservationStatus[rentStatus][type];
    setButtonText(statusConfig.buttonText);
    setButtonType(statusConfig.buttonType);
    setButtonClick(() => statusConfig.buttonClick);
  }, [rentStatus]);

  const updateToCancel = async (reservation: ReservationData) => {
    const response = await server.patch<ResponseWithoutData>('/reservations/' + reservation.id, {
      data: {
        status: reservationStatus.CANCEL,
      },
    });
    if (response && !response.success) {
      //TODO: 에러나면 어떻게 할지 논의 필요
    } else {
      setRentStatus('CANCEL');
    }
  };

  const getCurrentLocation = () => {
    return new Promise<GeolocationPosition>((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by this browser.'));
      } else {
        navigator.geolocation.getCurrentPosition(resolve, reject);
      }
    });
  };

  const updateToTerminated = async (reservation: ReservationData) => {
    try {
      // 현재 위치를 비동기적으로 가져옴
      const position = await getCurrentLocation();
      const { latitude, longitude } = position.coords;
      if (reservation.target.lat && reservation.target.lng) {
        const dist = distance(latitude, longitude, reservation.target.lat, reservation.target.lng);
        if (dist <= 0.2) {
          const response = await server.patch<ResponseWithoutData>('/reservations/' + reservation.id, {
            data: {
              status: reservationStatus.TERMINATED,
            },
          });

          if (response && !response.success) {
            //TODO: 에러 처리
          } else {
            setRentStatus('TERMINATED');
          }
        }
        //TODO: 너무 멀어서 반납 못한다고 표시
      }
    } catch (error) {
      //Todo: 위치 정보 획득 실패 모달 띄우기
    }
  };

  const buttonByReservationStatus: {
    [key in ReservationStatus]: {
      [key in TargetType]: { buttonText: string; buttonType: ButtonType; buttonClick?: ((reservation: ReservationData) => void) | null };
    };
  } = {
    CANCEL: {
      host: { buttonText: '거절완료', buttonType: 'disabled', buttonClick: null },
      guest: { buttonText: '취소됨', buttonType: 'disabled', buttonClick: null },
    },
    READY: {
      host: { buttonText: '거절하기', buttonType: 'danger', buttonClick: updateToCancel },
      guest: { buttonText: '예약취소', buttonType: 'danger', buttonClick: updateToCancel },
    },
    USING: {
      host: { buttonText: '대여중', buttonType: 'disabled', buttonClick: null },
      guest: { buttonText: '반납하기', buttonType: 'enabled', buttonClick: updateToTerminated },
    },
    TERMINATED: {
      host: { buttonText: '반납완료', buttonType: 'disabled', buttonClick: null },
      guest: { buttonText: '사용완료', buttonType: 'disabled', buttonClick: null },
    },
    UNDEFINED: {
      host: { buttonText: '', buttonType: 'disabled', buttonClick: null },
      guest: { buttonText: '', buttonType: 'disabled', buttonClick: null },
    },
  };

  return (
    <div
      className={`flex flex-col rounded-3xl bg-white px-6 py-4 text-sm shadow-md md:text-base ${className}`}>
      <ul role='list'>
        <li key='person.email'>
          <div className='flex gap-4 items-center justify-between'>
            <Link to={type === 'guest' ? `/cars/${reservation.target.id}` : `/profile/${reservation.target.id}`} className='flex gap-4 items-center'>
              <img
                  className={`bg-gray-50 h-12 max-w-12 ${type === 'host' ? 'rounded-full' : 'min-h-24 min-w-24 rounded-lg'}`}
                  src={reservation.target.image}
                  alt=''
                  onError={(e) => {
                    const imgElement = e.target as HTMLImageElement;
                    imgElement.src = '/defaultProfile.png';
                  }}
                />
                <div className='flex flex-col gap-2'>
                  <div className='flex gap-1 items-center'>
                    <p className='text-md font-semibold'>{reservation.target.name}</p>
                    <div className='flex gap-1 items-center'>
                      <StarIcon filled={true} className='w-4 h-4' />
                      <div className='text-sm'>{reservation.target.averageRate ?? 0}</div>
                    </div>
                  </div>
                  <div>
                    <p className='truncate text-xs text-background-500'>{reservation.target.address}</p>
                    <p className='break-all text-xs text-background-500'>{reservation.target.phoneNumber}</p>
                    <p className='text-xs text-background-500 whitespace-nowrap'>
                      {`${dateTimeFormatter(reservation.rentPeriod[0])} 
                      ~ ${dateTimeFormatter(reservation.rentPeriod[1])}`}
                    </p>
                  </div>
              </div>
            </Link>
            <div className='flex flex-col gap-3'>
              <div className='flex flex-col text-md text-right font-semibold mr-3'>
                  <p>
                    {reservation.rentFee.toLocaleString('ko-KR') || null}원
                  </p>
                  {reservation.rentStatus === 'USING' && extraFee > 0 && type === 'guest' && (
                    <p className='text-danger'>{'+' + extraFee.toLocaleString('ko-KR') || null}원</p>
                  )}
                  {reservation.rentStatus === 'TERMINATED' && reservation.extraFee > 0 && (
                    <p>
                      {'+' + reservation.extraFee.toLocaleString('ko-KR') || null}원
                    </p>
                  )}
              </div>
              <div className='flex gap-2'>
                <Button
                  type={buttonType}
                  text={buttonText}
                  onClick={() => buttonClick && buttonClick(reservation)}></Button>
                  {isReviewed || reservation.rentStatus !== 'TERMINATED' 
                  ? <></> 
                  : <Button text='리뷰 작성' onClick={reviewOnClick}/>}
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  );
}

export default ListComponent;
