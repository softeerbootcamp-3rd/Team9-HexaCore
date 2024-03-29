import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import Button from '@/components/Button';
import ListComponent from '@/components/ListComponent';
import HostCalendar from '@/components/calendar/hostCalendar/HostCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { TargetType } from '@/components/ListComponent';
import { server } from '@/fetches/common/axios';
import type { ResponseWithoutData } from '@/fetches/common/response.type';
import ImageGallery from '@/components/ImageGallery';
import { dateRangesToString } from '@/utils/converters';
import { createPortal } from 'react-dom';
import ReviewModal from '@/components/review/ReviewModal';
import { HostManageLoaderData } from './hostsRoutes';
import { useCustomToast } from '@/components/Toast';
import * as ChannelService from '@channel.io/channel-web-sdk-loader';
import { ReservationData } from '@/fetches/reservations/Reservation.type';

const TABS = ['calendar', 'reservation'] as const;
type TabType = (typeof TABS)[number];

function HostManage() {
  const navigate = useNavigate();
  const { carDetail, reservations } = useLoaderData() as HostManageLoaderData;
  const [selectedTab, setSelectedTab] = useState<TabType>('calendar');

  const [availableDates, setAvailableDates] = useState<DateRange[]>(carDetail?.carDateRanges ?? []);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalData, setModalData] = useState<ReservationData | null>(null);

	const { ToastComponent, showToast } = useCustomToast();


  // 채널톡 관련 스크립트 로딩
  useEffect(() => {
    if (!carDetail) return;
    ChannelService.loadScript();
    ChannelService.boot({
      pluginKey: import.meta.env.VITE_CHANNEL_TALK_API_KEY,
      memberId: carDetail.host.id.toString(), // 유일한 값이라서 id로 지정.
      unsubscribeEmail: true,
      unsubscribeTexting: true,
      profile: {
        name: carDetail.host.name,
        email: carDetail.host.email,
      },
    });
    return () => ChannelService.shutdown();
  }, []);

  if (!carDetail) {
    return '';
  }
  
  const editCar = () => {
    navigate('/hosts/register');
  };

  const deleteCar = async () => {
    const response = await server.delete<ResponseWithoutData>('/cars/' + carDetail.id, {});
    if (response.success) {
      location.reload();
    }
  };

  const updateDates = async () => {
    const response = await server.put<ResponseWithoutData>('/cars/' + carDetail?.id + '/date', {
      data: {
        dates: dateRangesToString(availableDates),
      },
    });

    if (!response || !response.success) {
      showToast('수정 실패', response.message ?? '예약 가능한 날짜 구간 수정이 실패하였습니다.')
    } else {
      showToast('수정 성공', '예약 가능 날짜 수정이 반영되었습니다.', true);
    }
  };

  const handleTabSelect = (tab: TabType) => {
    setSelectedTab(tab);
  };

  // 선택된 탭에 따라 해당 컴포넌트 렌더링
  const renderSelectedComponent = () => {
    const reservationDates = reservations.map((reservation) => reservation.rentPeriod);

    switch (selectedTab) {
      case 'calendar':
        return (
          <div className='rounded-xl bg-white p-8'>
            <div className=''>
              <HostCalendar size='large' availableDates={availableDates} onAvailableDatesChange={setAvailableDates} reservations={reservationDates} />
              <div className='flex justify-end'>
                <Button text='저장' onClick={updateDates}></Button>
              </div>
            </div>
          </div>
        );
      case 'reservation':
        return ReservationCard; // TODO: reservations를 이용한 동적 컴포넌트로 변경
      default:
        return null;
    }
  };

  const ReservationCard = reservations.length !== 0 ? reservations.map((reservation, index) => (
    <ListComponent
      key={index}
      type={'host' as TargetType}
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
    <div className='flex min-w-[768px] flex-col gap-8'>
      <h2 className='mt-4 pl-3 text-3xl font-semibold'>{`${carDetail.host.name}님, 등록한 차량을 관리해보세요!`}</h2>
      <div className='mb-10 flex gap-8'>
        {/* Car Info Manage */}
        <div className='flex w-1/2 flex-col gap-3'>
          <h3 className='pl-3 text-lg font-medium xl:text-xl'>차량 정보</h3>
          <div className='flex w-full flex-col overflow-hidden rounded-3xl bg-white shadow-xl'>
            {/* Image Gallery */}
            <ImageGallery imageUrls={carDetail.imageUrls} className='h-[300px] w-full rounded-xl' />

            <div className='flex flex-col justify-center gap-4 p-6'>
              {/* Header : CarName, CarNumber, type, mileage, fuel */}
              <div className='flex flex-col gap-2'>
                <div className='flex justify-between'>
                  <div className='flex items-center gap-2 '>
                    <h1 className='text-base font-bold lg:text-xl'>{carDetail.categoryName}</h1>
                    <div className='font-media color flex h-full flex-col justify-end text-xs text-background-700 lg:text-sm'>{carDetail.carNumber}</div>
                    {/* <Tag className="h-6 text-background-700 font-semibold text-base" text={data.car.carNumber} /> */}
                  </div>
                  <div className='ml-3 flex items-center gap-3'>
                    <Button
                      className='flex h-8 flex-col items-center justify-center whitespace-nowrap text-xs xl:text-sm'
                      text='수정'
                      onClick={editCar}></Button>
                    <Button
                      className='flex h-8 flex-col items-center justify-center whitespace-nowrap text-xs xl:text-sm'
                      text='삭제'
                      type='danger'
                      onClick={deleteCar}></Button>
                  </div>
                </div>
                <p className='text-background-500'>
                  차종 {carDetail.type} &#183; 연비 {carDetail.mileage.toString()}km/L &#183; 연료 {carDetail.fuel}
                </p>
              </div>
              {/* Line */}
              <div className='h-0 w-full border-[0.5px] border-background-300'></div>
              {/* Car Info Detail */}
              <div className='flex flex-col gap-3'>
                {/* carNumber */}
                <div className='flex items-center gap-4'>
                  <div className='bg-gray-300 flex h-8 w-8 items-center justify-center overflow-hidden rounded-full'>
                    <img src='/location.svg' alt='host-profile' />
                  </div>
                  <div className='flex flex-col'>
                    <p className='font-semibold'>위치</p>
                    <p className='text-sm text-background-500'>{carDetail.address}</p>
                  </div>
                </div>

                {/* Year */}
                <div className='flex items-center gap-4'>
                  <div className='bg-gray-300 flex h-8 w-8 items-center justify-center overflow-hidden rounded-full'>
                    <img src='/price.svg' alt='host-profile' />
                  </div>
                  <div className='flex flex-col'>
                    <p className='font-semibold'>가격</p>
                    <p className='text-sm text-background-500'>{carDetail.feePerHour?.toString()}원 / 시간</p>
                  </div>
                </div>

                {/* Address */}
                <div className='flex items-center gap-4'>
                  <div className='bg-gray-300 flex h-8 w-8 items-center justify-center overflow-hidden rounded-full'>
                    <img src='/year.svg' alt='host-profile' />
                  </div>
                  <div className='flex flex-col'>
                    <p className='font-semibold'>연식</p>
                    <p className='text-sm text-background-500'>{carDetail.year?.toString()}년</p>
                  </div>
                </div>
              </div>

              {/* Line */}
              <div className='h-0 w-full border-[0.5px] border-background-300'></div>

              {/* Description */}
              <div className='px-2'>
                <p className='whitespace-pre-line leading-8 text-background-500'>{carDetail.description}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Calendar + Reservation */}
        <div className='flex w-1/2 flex-col gap-3'>
          {/* Button div */}
          <div className='flex gap-6'>
            <button
              className={`bg-gray-200 w-fit whitespace-nowrap text-lg xl:text-xl ${
                selectedTab === 'calendar' ? 'font-bold text-black' : 'text-background-300'
              } pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('calendar')}>
              예약 가능 일자
            </button>
            <button
              className={`bg-gray-200 w-fit whitespace-nowrap text-lg xl:text-xl ${
                selectedTab === 'reservation' ? 'font-bold text-black' : 'text-background-300'
              } pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('reservation')}>
              내 차 예약 내역
            </button>
          </div>
          <div className='flex flex-col gap-4 pr-6 pb-3'>
            {renderSelectedComponent()}
            {isModalOpen && modalData &&
              createPortal(
                <ReviewModal
                  type={'host' as TargetType}
                  onClose={() => setIsModalOpen(false)}
                  reservation={modalData}
                  finished={() => {
                    showToast('리뷰 작성 성공', '작성하신 리뷰가 등록되었습니다. 감사합니다.', true);
                    modalData.isReviewed = true;
                  }}
                />,
                document.body,
              )}
          </div>
        </div>
      </div>
      <ToastComponent />
    </div>
  );
}

export default HostManage;

