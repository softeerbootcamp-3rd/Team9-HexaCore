import Button from '@/components/Button';
import { UserData } from '@/fetches/users/fetchUser';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import PhoneIcon from '@/components/svgs/PhoneIcon';
import MailIcon from '@/components/svgs/MailIcon';
import { deleteUser } from '@/fetches/auth/fetchAuth';
import ListComponent, { TargetType } from '@/components/ListComponent';

function Profile() {
  const data = useLoaderData() as { user: UserData; reservations: ReservationData[] };
  const navigator = useNavigate();
  if (!data.user) {
    return "";
  }

  const editProfile = () => {
    navigator('/auth/signup/'+localStorage.getItem("userId"));
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
      
      <div className='flex pt-3 h-1/3 min-h-[190px] flex-row'>
        <h2 className='text-lg font-bold w-[10%]'>내 정보</h2>
      
        <div className='flex pt-2 w-full h-auto items-start'>
          <img className='rounded-2xl w-[150px] h-[150px] shadow-md' src={data.user?.image || '../public/defaultProfile.png'}></img>
          
          <div className='ml-8 flex flex-col w-2/5'>
            <p className='text-md font-semibold m-1 py-1'>
              {data.user?.name}
            </p>

            <div className='flex flex-row text-background-400 items-center m-1'>
              <MailIcon />
              <p className='text-sm ml-4'>
                {data.user?.email}
              </p>
            </div>

            <div className='flex flex-row text-background-400 items-center m-1'>
              <PhoneIcon />
              <p className='text-sm ml-4'>          
                {data.user?.phoneNum}
              </p>
            </div>

            <div className='p-3 pl-0 pb-0 flex flex-row'>
              <Button
                text='수정'
                className='flex h-8 w-1/6 items-center justify-center whitespace-nowrap text-xs xl:text-sm rounded-xl'
                onClick={editProfile}>
              </Button>
              <Button
                text='탈퇴'
                className='ml-5 flex h-8 w-1/6 items-center justify-center whitespace-nowrap text-xs xl:text-sm rounded-xl'
                onClick={deleteUser}
                type='danger'>
              </Button>
            </div>
          </div>
          
        </div>
      </div>

      <hr className='border-background-200 pb-3'></hr>
      
      <div className='h-2/3 mt-8 '>
        <div className='flex h-full w-full'>
          <h2 className='text-lg font-bold w-[10%]'>예약 내역</h2>
          <div className='flex max-h-[560px] grow flex-col gap-5 overflow-y-auto pr-6'>
            {ReservationCard}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
