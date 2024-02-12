import Button from '@/components/Button';
import { useEffect } from 'react';
import { UserInfo } from '@/pages/profile/profileRoutes';
import { Reservation } from '@/types/Reservations';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import ListComponent from '@/components/ListComponent';

function Profile() {
  const data = useLoaderData() as { user: UserInfo; reservations: Reservation[] };
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
    const dateA = a.startDate;
    const dateB = b.startDate;
    if (dateA && dateB) {
      if (dateA < dateB) {
        return 1;
      } else if (dateA > dateB) {
        return -1;
      } else if (a.status && b.status) {
        const statusOrder = ['using', 'ready', 'cancel', 'terminated'];
        const statusIndexA = statusOrder.indexOf(a.status);
        const statusIndexB = statusOrder.indexOf(b.status);
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
            startDate: reservation.startDate ?? new Date(),
            endDate: reservation.endDate ?? new Date(),
            status: reservation.status ?? '',
            price: reservation.fee ?? undefined,
            address: reservation.address ?? '',
          }}
        />
      ))
    : null;
  return (
    <div className="min-w-[640px] flex flex-col h-full">
      <div className="h-1/4">
        <h2 className="text-lg font-bold">내 정보</h2>
        <div className="my-6 flex h-auto w-full">
          <img className="h-40 w-32 rounded-2xl" src={data.user?.image || '../public/default-profile.png'}></img>
          <div className="ml-6 flex w-2/5 flex-col">
            <p className="text-md font-bold">
              {data.user?.name} ({data.user?.nickName})
            </p>
            <p className="mt-3 text-sm text-background-400">{data.user?.email}</p>
            <p className="text-sm text-background-400">{data.user?.phoneNum}</p>
            <Button text="수정" className="h-10 m-6 ml-0 w-1/4 text-xs xl:text-sm whitespace-nowrap" onClick={editProfile}></Button>
          </div>
        </div>
      </div>
      <div className="h-2/3">
        <hr className="border-background-300"></hr>
        <div className="flex h-full w-full">
          <h2 className="mt-8 text-lg font-bold">예약 내역</h2>
          <div className="flex flex-col grow max-h-[560px] gap-5 pr-6 overflow-y-auto mx-6 mt-8">{ReservationCard}</div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
