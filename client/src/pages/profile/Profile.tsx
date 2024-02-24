import Button from '@/components/Button';
import { UserData } from '@/fetches/users/fetchUser';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { Link, useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import { fetchGuestReservations, parseGuestReservations } from '@/fetches/reservations/fetchGuestReservations';
import PhoneIcon from '@/components/svgs/PhoneIcon';
import MailIcon from '@/components/svgs/MailIcon';
import ListComponent, { TargetType } from '@/components/ListComponent';
import { useEffect, useRef, useState } from 'react';
import { server } from '@/fetches/common/axios';
import { useAuth } from '@/contexts/AuthContext';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import { createPortal } from 'react-dom';
import ReviewModal from '@/components/review/ReviewModal';
import { useCustomToast } from '@/components/Toast';

function Profile() {
  const user = useLoaderData() as UserData;
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
	const { ToastComponent, showToast } = useCustomToast();

  useEffect(() => {
    if (!user) {
      navigate('/auth/login');
    }
  }, [user, navigate]);

  const { auth, setAuth } = useAuth();
  const loaderRefNext = useRef(null);
  const [reservations, setReservations] = useState<ReservationData[]>([]);
  const [modalData, setModalData] = useState<ReservationData | null>(null);
  let hasNext = false;
  const page = useRef(0);

  const fetchReservations = async () => {
    const response = await fetchGuestReservations(page.current, 10);
    if (response && response.success) {
      const newReservations = parseGuestReservations(response.data);
      hasNext = response.pageInfo.hasNext;
      setReservations((prevReservations) => [...prevReservations, ...newReservations]);
    }
  };

  const observerNext = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      if (page.current !== 0 && !hasNext) return;
      fetchReservations();
      page.current += 1;
    });
  });
  useEffect(() => {
    if (loaderRefNext.current) {
      observerNext.observe(loaderRefNext.current);
    }
  }, []);
  const editProfile = () => {
    navigate('/auth/signup/' + localStorage.getItem('userId'));
    return;
  };
  const deleteUser = async () => {
    const response = await server.delete<ResponseWithoutData>('/auth');


    if (!response.success) {
      // 회원탈퇴 실패한 경우
      alert(response.message); // TODO: 실패 시 처리
    }
    // TODO: 성공 시 처리
    setAuth({
      userId: null,
      accessToken: null,
      refreshToken: null,
    });
    navigate('/');
  };
  const ReservationCard = reservations && reservations.length !== 0
    ? reservations.map((reservation, index) => (
      <ListComponent
        key={index}
        type={'guest' as TargetType}
        reservation={reservation}
        reviewOnClick={() => {
          setIsModalOpen(true);
          setModalData(reservation);
        }}
        isReviewed={reservation.isReviewed}
      />
    ))
    : <div className='flex justify-center items-center h-full text-background-400'>
      예약 내역이 없습니다.
    </div>;

  return (
    <div className='flex h-full min-w-[640px] flex-col overflow-hidden'>
      <div className='flex h-1/4 min-h-[170px] flex-row'>
        <h2 className='w-[10%] text-lg font-bold'>내 정보</h2>

        <div className='flex h-auto w-full items-start pt-2'>
          <img className='h-[150px] w-[150px] rounded-2xl shadow-md' src={user?.image || '/defaultProfile.png'}></img>

          <div className='ml-8 flex w-2/5 flex-col'>
            <p className='text-md m-1 py-1 font-semibold'>{user?.name}</p>

            <div className='m-1 flex flex-row items-center text-background-400'>
              <MailIcon />
              <p className='ml-4 text-sm'>{user?.email}</p>
            </div>

            <div className='m-1 flex flex-row items-center text-background-400'>
              <PhoneIcon />
              <p className='ml-4 text-sm'>{user?.phoneNum}</p>
            </div>

            <div className='flex flex-row pt-3 gap-2'>
              <Link to={`/auth/signup/${auth.userId}`} className='h-8 w-1/6 '>
                <Button text='수정' className='whitespace-nowrap rounded-xl text-xs xl:text-sm' onClick={editProfile}/>
              </Link>
              <Button
                text='탈퇴'
                className='flex h-8 items-center justify-center whitespace-nowrap rounded-xl text-xs xl:text-sm'
                onClick={deleteUser}
                type='danger'
              />
            </div>
          </div>
        </div>
      </div>

      <hr className='border-background-200'></hr>

      <div className='w-full mt-4 flex h-2/3'>
        <h2 className='w-[10%] text-lg font-bold'>예약 내역</h2>
        <div className='flex grow flex-col gap-5 overflow-y-scroll scrollbar-hide pb-5 pr-6'>
          {ReservationCard}
          {isModalOpen && modalData &&
            createPortal(
              <ReviewModal
                type={'guest' as TargetType}
                onClose={() => setIsModalOpen(false)}
                reservation={modalData}
                finished={() => {
                  showToast('리뷰 작성 성공', '작성하신 리뷰가 등록되었습니다. 감사합니다.', true);
                  modalData.isReviewed = true;
                }}
              />,
              document.body,
            )}
          <div ref={loaderRefNext}></div>
        </div>
        <ToastComponent />
      </div>
    </div>
  );
}

export default Profile;

