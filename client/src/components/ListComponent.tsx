import { useEffect } from 'react';
import Button from './Button';
import { ReservationStatus } from '@/fetches/reservations/fetchHostReservations';

type ButtonType = 'disabled' | 'enabled' | 'danger';
export type TargetType = 'host' | 'guest';
type Props = {
  target: { type: TargetType; name: string; phoneNumber: string; image: string };
  reservation: { startDate: Date; endDate: Date; status: ReservationStatus; price: number; address: string };
  className?: string;
};
const dateFormatter = new Intl.DateTimeFormat('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });

const buttonByReservationStatus: {
  [key in ReservationStatus]: {
    [key in TargetType]: { buttonText: string; buttonType: ButtonType };
  };
} = {
  CANCEL: {
    host: { buttonText: '거절완료', buttonType: 'disabled' },
    guest: { buttonText: '예약실패', buttonType: 'disabled' },
  },
  READY: {
    host: { buttonText: '거절하기', buttonType: 'danger' },
    guest: { buttonText: '대여시작', buttonType: 'enabled' },
  },
  USING: {
    host: { buttonText: '반납확인', buttonType: 'enabled' },
    guest: { buttonText: '대여중', buttonType: 'disabled' },
  },
  TERMINATED: {
    host: { buttonText: '반납완료', buttonType: 'disabled' },
    guest: { buttonText: '사용완료', buttonType: 'disabled' },
  },
};

function ListComponent({ target, reservation, className }: Props) {
  const { buttonText, buttonType } = buttonByReservationStatus[reservation.status][target.type];
  useEffect(() => {
    const handleResize = () => {
      const buttons = document.querySelectorAll('Button');
      buttons.forEach((button) => {
        if (window.innerWidth <= 1000) {
          button.classList.add('w-[9ch]', 'min-w-[9ch]');
        } else {
          button.classList.remove('w-[9ch]', 'min-w-[9ch]');
        }
      });
    };
    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);
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
                className={`bg-gray-50 h-12 w-12 flex-none ${target.type === 'host' ? 'ml-6 rounded-full' : 'min-h-24 min-w-24 rounded-lg'}`}
                src={target.image}
                alt=''
                onError={(e) => {
                  const imgElement = e.target as HTMLImageElement;
                  imgElement.src = '../public/default-profile.png';
                }}
              />
              <div className='flex flex-col justify-between'>
                <p className='text-md leading-60 font-semibold'>{target.name}</p>
                <div>
                  <p className='break-all text-xs leading-5 text-background-500'>{target.phoneNumber}</p>
                  {target.type === 'guest' && (
                    <>
                      <p className='truncate text-xs leading-5 text-background-500'>{reservation.address}</p>
                      <p className='text-xs leading-6 text-background-500'>
                        {`${dateFormatter.format(reservation.startDate).replace(/ /g, '').replace(/\.$/, '')} 
                        ~ ${dateFormatter.format(reservation.endDate).replace(/ /g, '').replace(/\.$/, '')}`}
                      </p>
                    </>
                  )}
                </div>
              </div>
            </div>
            <div className={`ml-6 flex h-full  w-1/2 ${target.type === 'guest' ? 'flex-col items-end justify-end' : 'items-center justify-end'}`}>
              <div className='mr-6 flex w-1/2 flex-col'>
                {target.type === 'host' && (
                  <p className='text-right text-xs leading-6 text-background-500'>
                    {`${dateFormatter.format(reservation.startDate).replace(/ /g, '').replace(/\.$/, '')} 
                    ~ ${dateFormatter.format(reservation.endDate).replace(/ /g, '').replace(/\.$/, '')}`}
                  </p>
                )}
                <p className={`text-md truncate text-right font-semibold leading-5 ${target.type === 'guest' ? 'mb-2' : ''}`}>
                  {reservation.price || undefined}원
                </p>
              </div>
              <div className={`flex justify-end ${target.type === 'guest' ? 'w-full' : ''}`}>
                {reservation.status === 'READY' && target.type === 'guest' && (
                  <Button className='mr-6 h-auto w-1/4 rounded-xl text-xs lg:text-sm' type='danger' text='예약취소'></Button>
                )}
                <Button
                  className={`mr-6 h-auto rounded-xl text-xs lg:text-sm ${target.type === 'guest' ? 'w-1/4 ' : 'w-full'}`}
                  type={buttonType}
                  text={buttonText}></Button>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  );
}

export default ListComponent;
