import Button from '@/components/Button';
import { useEffect } from 'react';
import { UserData } from '@/fetches/users/fetchUser';
import { ReservationData } from '@/fetches/reservations/fetchHostReservations'
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
    <div className="min-w-[640px] flex flex-col h-full">
      <div className="h-auto flex flex-col">
        <h2 className="text-lg font-bold">내 정보</h2>
        <div className="py-4 flex h-auto w-full">
          <img className="h-28 w-24 rounded-2xl" src={data.user?.image || '../public/default-profile.png'}></img>
          <div className="ml-6 flex w-2/5 flex-col">
            <p className="text-md font-bold">
              {data.user?.name} ({data.user?.nickName})
            </p>
            <p className="mt-2 text-sm text-background-400">{data.user?.email}</p>
            <p className="text-sm text-background-400">{data.user?.phoneNum}</p>
            <Button text="수정" className="h-8 m-4 ml-0 w-1/5 text-xs xl:text-sm whitespace-nowrap" onClick={editProfile}></Button>
          </div>
        </div>
      </div>
      <hr className="border-background-300"></hr>
      <div className="h-2/3">
        <div className="flex h-full w-full">
          <h2 className="mt-8 text-lg font-bold">예약 내역</h2>
          <div className="flex flex-col grow max-h-[560px] gap-5 pr-6 overflow-y-auto mx-6 mt-8">{ReservationCard}</div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
