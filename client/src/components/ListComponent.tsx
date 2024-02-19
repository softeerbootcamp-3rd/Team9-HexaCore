import Button from './Button';
import { ReservationData, ReservationStatus, reservationStatus } from '@/fetches/reservations/Reservation.type';
import { server } from '@/fetches/common/axios';
import type { ResponseWithoutData } from '@/fetches/common/response.type';
import { useEffect, useState } from 'react';

type ButtonType = 'disabled' | 'enabled' | 'danger';
export type TargetType = 'host' | 'guest';
type Props = {
  type: TargetType;
  reservation: ReservationData;
  className?: string;
};
const dateFormatter = new Intl.DateTimeFormat('en-US', { year: 'numeric', month: '2-digit', day: '2-digit', hour:'2-digit' });

function ListComponent({ type, reservation, className }: Props) {
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
        const hours = Math.floor((difference / (1000 * 60 * 60)));
        setTimeDifference(hours*-1);
      }
    };

    // 60초마다 시간 차이 업데이트
    const interval = setInterval(updateTimeDifference, 60000);

    // 초기 로드 시에도 바로 업데이트
    updateTimeDifference();

    // 컴포넌트 언마운트 시 인터벌 정리
    return () => clearInterval(interval);
  }, [reservation.rentPeriod]);
  
  function calculateExtraFee(overTime:number) {
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
        reject(new Error("Geolocation is not supported by this browser."));
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
      
      console.log(latitude);
      console.log(longitude);
      // const response = await server.patch<ResponseWithoutData>('/reservations/' + reservation.id, {
      //   data: {
      //     status: reservationStatus.TERMINATED,
      //   },
      // });
  
      // if (response && !response.success) {
      //   //TODO: 에러 처리
      // } else {
      //   setRentStatus('TERMINATED');
      // }
    } catch (error) {
      console.error("Error getting location", error);
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
      className={`
		flex flex-col rounded-3xl bg-white p-4 text-sm shadow-md md:text-base
		${className}`}>
      <ul role='list' className='divide-gray-100 min-h-20 divide-y'>
        <li key='person.email' className='h-full'>
          <div className='flex h-full w-full items-center justify-between'>
            <div className='flex h-full min-w-0 items-center gap-x-4'>
              <img
                className={`bg-gray-50 h-12 w-12 flex-none ${type === 'host' ? 'ml-6 rounded-full' : 'min-h-24 min-w-24 rounded-lg'}`}
                src={reservation.target.image}
                alt=''
                onError={(e) => {
                  const imgElement = e.target as HTMLImageElement;
                  imgElement.src = '/defaultProfile.png';
                }}
              />
              <div className='flex flex-col justify-between'>
                <p className='text-md leading-60 font-semibold'>{reservation.target.name}</p>
                <div>
                  <p className='break-all text-xs leading-5 text-background-500'>{reservation.target.phoneNumber}</p>
                  {type === 'guest' && (
                    <>
                      <p className='truncate text-xs leading-5 text-background-500'>{reservation.address}</p>
                      <p className='text-xs leading-6 text-background-500'>
                        {`${dateFormatter.format(reservation.rentPeriod[0]).replace(/ /g, '').replace(/\.$/, '').replace(/\,/, ' ')} 
                        ~ ${dateFormatter.format(reservation.rentPeriod[1]).replace(/ /g, '').replace(/\.$/, '').replace(/\,/, ' ')}`}
                      </p>
                    </>
                  )}
                </div>
              </div>
            </div>
            <div className={`ml-6 flex h-full  w-1/2 ${type === 'guest' ? 'flex-col items-end justify-end' : 'items-center justify-end'}`}>
              <div className='mr-6 flex w-1/2 flex-col'>
                {type === 'host' && (
                  <p className='text-right text-xs leading-6 text-background-500'>
                    {`${dateFormatter.format(reservation.rentPeriod[0]).replace(/ /g, '').replace(/\.$/, '').replace(/\,/, ' ')} 
                    ~ ${dateFormatter.format(reservation.rentPeriod[1]).replace(/ /g, '').replace(/\.$/, '').replace(/\,/, ' ')}`}
                  </p>
                )}
                <p className={`text-md truncate text-right font-semibold leading-5 ${type === 'guest' ? 'mb-2' : ''}`}>{reservation.rentFee || null}원</p>
                {reservation.rentStatus === 'USING' && extraFee > 0 && (
                  <p className={`text-md truncate text-right font-semibold leading-5 text-danger ${type === 'guest' ? 'mb-2' : ''}`}>{extraFee || null}원</p>
                )}
              </div>
              <div className={`flex justify-end ${type === 'guest' ? 'w-full' : ''}`}>
                <Button
                  className={`mr-6 h-auto w-[9ch] min-w-[9ch] rounded-xl text-xs lg:text-sm ${type === 'guest' ? 'xl:w-1/4 ' : 'xl:w-full'}`}
                  type={buttonType}
                  text={buttonText}
                  onClick={() => buttonClick && buttonClick(reservation)}></Button>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  );
}

export default ListComponent;

