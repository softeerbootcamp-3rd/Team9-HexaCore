import { useLoaderData } from 'react-router';
import { useEffect } from 'react';
import Button from '@/components/Button';
import { UserData } from '@/fetches/users/fetchUser';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { Link, useNavigate } from 'react-router-dom';
import PhoneIcon from '@/components/svgs/PhoneIcon';
import MailIcon from '@/components/svgs/MailIcon';
import ListComponent, { TargetType } from '@/components/ListComponent';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import { useAuth } from '@/contexts/AuthContext';

function Profile() {
  const data = useLoaderData() as { user: UserData; reservations: ReservationData[] };
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!data.user) {
      navigate('/auth/login');
    }
  }, [data.user, navigate]);

  const onDeleteUser = async () => {
    const response = await server.delete<ResponseWithoutData>('/auth');


    if (!response.success) {
      // 회원탈퇴 실패한 경우
      alert(response.message); // TODO: 실패 시 처리
    }
    setAuth({
      userId: null,
      accessToken: null,
      refreshToken: null,
    });
    navigate('/');
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
          type={'guest' as TargetType}
          reservation={{
            id: reservation.id,
            target: reservation.target,
            rentPeriod: reservation.rentPeriod,
            rentStatus: reservation.rentStatus ?? '',
            rentFee: reservation.rentFee ?? null,
            address: reservation.address ?? '',
          }}
        />
      ))
    : null;
  return (
    <div className='flex h-full min-w-[640px] flex-col'>
      <div className='flex h-1/3 flex-row pt-3'>
        <h2 className='w-[10%] text-lg font-bold'>내 정보</h2>

        <div className='flex h-auto w-full items-start pt-2'>
          <img className='h-[150px] w-[150px] rounded-2xl shadow-md' src={data.user?.image || '../public/defaultProfile.png'}></img>

          <div className='ml-8 flex w-2/5 flex-col'>
            <p className='text-md m-1 py-1 font-semibold'>{data.user?.name}</p>

            <div className='m-1 flex flex-row items-center text-background-400'>
              <MailIcon />
              <p className='ml-4 text-sm'>{data.user?.email}</p>
            </div>

            <div className='m-1 flex flex-row items-center text-background-400'>
              <PhoneIcon />
              <p className='ml-4 text-sm'>{data.user?.phoneNum}</p>
            </div>

            <div className='flex flex-row p-3 pb-0 pl-0'>
              <Link to={`/auth/signup/${auth.userId}`} className='h-8 w-1/6 '>
                <Button text='수정' className='whitespace-nowrap rounded-xl text-xs xl:text-sm' />
              </Link>
              <Button
                text='탈퇴'
                className='ml-5 flex h-8 w-1/6 items-center justify-center whitespace-nowrap rounded-xl text-xs xl:text-sm'
                onClick={onDeleteUser}
                type='danger'
              />
            </div>
          </div>
        </div>
      </div>

      <hr className='border-background-200 pb-3'></hr>

      <div className='mt-8 h-2/3 '>
        <div className='flex h-full w-full'>
          <h2 className='w-[10%] text-lg font-bold'>예약 내역</h2>
          <div className='flex max-h-[560px] grow flex-col gap-5 overflow-y-auto pr-6'>{ReservationCard}</div>
        </div>
      </div>
    </div>
  );
}

export default Profile;

