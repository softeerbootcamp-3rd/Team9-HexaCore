import Button from '@/components/Button';
import { useEffect } from 'react';
import { UserData } from '@/fetches/users/fetchUser';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import ListComponent from '@/components/ListComponent';

function Profile() {
  const data = useLoaderData() as { user: UserData; reservations: ReservationData[] };
  const navigator = useNavigate();
  useEffect(() => {
    if (!data.user) {
      navigator('/auth/login');
    }
  }, [data.user, navigator]);

  const editProfile = () => {
    navigator('/auth/signup');
    return;
  };

  const reservations = data.reservations.sort((a, b) => {
    const dateA = a.rentPeriod[0];
    const dateB = b.rentPeriod[0];
    if (dateA && dateB) {
      if (dateA < dateB) {
        return 1;
      } else if (dateA > dateB) {
        return -1;
      } else if (a.rentStatus && b.rentStatus) {
        const statusOrder = ['using', 'ready', 'cancel', 'terminated'];
        const statusIndexA = statusOrder.indexOf(a.rentStatus);
        const statusIndexB = statusOrder.indexOf(b.rentStatus);
        if (statusIndexA < statusIndexB) {
          return -1;
        } else if (statusIndexA > statusIndexB) {
          return 1;
        } else {
          return 0;
        }
      }
    }
    return 0;
  });

  const ReservationCard = data.reservations
    ? reservations.map((reservation, index) => (
        <ListComponent
          key={index}
          target={{
            type: 'guest',
            name: reservation.target.name ?? '',
            phoneNumber: reservation.target.phoneNumber ?? '',
            image: reservation.target.image ?? '',
          }}
          reservation={{
            startDate: reservation.rentPeriod[0] ?? new Date(),
            endDate: reservation.rentPeriod[1] ?? new Date(),
            status: reservation.rentStatus ?? '',
            price: reservation.rentFee ?? undefined,
            address: reservation.address ?? '',
          }}
        />
      ))
    : null;
  return (
    <div className='flex h-full min-w-[640px] flex-col'>
      <div className='flex h-auto flex-col'>
        <h2 className='text-lg font-bold'>내 정보</h2>
        <div className='flex h-auto w-full py-4'>
          <img className='h-28 w-24 rounded-2xl' src={data.user?.image || '../public/defaultProfile.png'}></img>
          <div className='ml-6 flex w-2/5 flex-col'>
            <p className='text-md font-bold'>{data.user?.name}</p>
            <p className='mt-2 text-sm text-background-400'>{data.user?.email}</p>
            <p className='text-sm text-background-400'>{data.user?.phoneNum}</p>
            <Button
              text='수정'
              className='m-4 ml-0 flex h-8 w-1/5 items-center justify-center whitespace-nowrap text-xs xl:text-sm'
              onClick={editProfile}></Button>
          </div>
        </div>
      </div>
      <hr className='border-background-300'></hr>
      <div className='h-2/3'>
        <div className='flex h-full w-full'>
          <h2 className='mt-8 text-lg font-bold'>예약 내역</h2>
          <div className='mx-6 mt-8 flex max-h-[560px] grow flex-col gap-5 overflow-y-auto pr-6'>{ReservationCard}</div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
