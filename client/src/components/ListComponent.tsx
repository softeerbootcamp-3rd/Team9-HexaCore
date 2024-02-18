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
const dateFormatter = new Intl.DateTimeFormat('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });

function ListComponent({ type, reservation, className }: Props) {
  const [buttonText, setButtonText] = useState('');
  const [buttonType, setButtonType] = useState<ButtonType>('disabled');
  const [buttonClick, setButtonClick] = useState<((reservation: ReservationData) => void) | undefined>(undefined);
  const [rentStatus, setRentStatus] = useState(reservation.rentStatus);

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

  const updateToUsing = async (reservation: ReservationData) => {
    const response = await server.patch<ResponseWithoutData>('/reservations/' + reservation.id, {
      data: {
        status: reservationStatus.USING,
      },
    });
    if (response && !response.success) {
      //TODO: 에러나면 어떻게 할지 논의 필요
    } else {
      setRentStatus('USING');
    }
  };

  const updateToTerminated = async (reservation: ReservationData) => {
    const response = await server.patch<ResponseWithoutData>('/reservations/' + reservation.id, {
      data: {
        status: reservationStatus.TERMINATED,
      },
    });
    if (response && !response.success) {
      //TODO: 에러나면 어떻게 할지 논의 필요
    } else {
      setRentStatus('TERMINATED');
    }
  };

  const buttonByReservationStatus: {
    [key in ReservationStatus]: {
      [key in TargetType]: { buttonText: string; buttonType: ButtonType; buttonClick?: (reservation: ReservationData) => void };
    };
  } = {
    CANCEL: {
      host: { buttonText: '거절완료', buttonType: 'disabled', buttonClick: undefined },
      guest: { buttonText: '취소됨', buttonType: 'disabled', buttonClick: undefined },
    },
    READY: {
      host: { buttonText: '거절하기', buttonType: 'danger', buttonClick: updateToCancel },
      guest: { buttonText: '대여시작', buttonType: 'enabled', buttonClick: updateToUsing },
    },
    USING: {
      host: { buttonText: '반납확인', buttonType: 'enabled', buttonClick: updateToTerminated },
      guest: { buttonText: '대여중', buttonType: 'disabled', buttonClick: undefined },
    },
    TERMINATED: {
      host: { buttonText: '반납완료', buttonType: 'disabled', buttonClick: undefined },
      guest: { buttonText: '사용완료', buttonType: 'disabled', buttonClick: undefined },
    },
    UNDEFINED: {
      host: { buttonText: '', buttonType: 'disabled', buttonClick: undefined },
      guest: { buttonText: '', buttonType: 'disabled', buttonClick: undefined },
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
                        {`${dateFormatter.format(reservation.rentPeriod[0]).replace(/ /g, '').replace(/\.$/, '')} 
                        ~ ${dateFormatter.format(reservation.rentPeriod[1]).replace(/ /g, '').replace(/\.$/, '')}`}
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
                    {`${dateFormatter.format(reservation.rentPeriod[0]).replace(/ /g, '').replace(/\.$/, '')} 
                    ~ ${dateFormatter.format(reservation.rentPeriod[1]).replace(/ /g, '').replace(/\.$/, '')}`}
                  </p>
                )}
                <p className={`text-md truncate text-right font-semibold leading-5 ${type === 'guest' ? 'mb-2' : ''}`}>{reservation.rentFee || undefined}원</p>
              </div>
              <div className={`flex justify-end ${type === 'guest' ? 'w-full' : ''}`} data-buttonArea>
                {rentStatus === 'READY' && type === 'guest' && (
                  <Button
                    className={`mr-6 h-auto w-[9ch] min-w-[9ch] rounded-xl text-xs lg:text-sm ${type === 'guest' ? 'xl:w-1/4 ' : 'xl:w-full'}`}
                    type='danger'
                    text='예약취소'
                    onClick={() => updateToCancel(reservation)}></Button>
                )}
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

