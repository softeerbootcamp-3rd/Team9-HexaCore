import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import Button from '@/components/Button';
import ListComponent from '@/components/ListComponent';
import HostCalendar from '@/components/calendar/hostCalendar/HostCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { HostManageLoaderData } from './hostsRoutes';
import ImageGallery from '../cars/ImageGallery';

const TABS = ['calendar', 'reservation'] as const;
type TabType = (typeof TABS)[number];

function HostManage() {
  const navigate = useNavigate();
  const { carDetail, hostReservations } = useLoaderData() as HostManageLoaderData;
  const [selectedTab, setSelectedTab] = useState<TabType>('calendar');
  const [availableDates, setAvailableDates] = useState<DateRange[]>(carDetail.carDateRanges);

  const editCar = () => {
    alert('수정이 완료되었습니다.');
    location.reload();
  };

  const deleteCar = () => {
    alert('삭제가 완료되었습니다.');
    location.reload();
  };

  if (carDetail === null) {
    navigate('/hosts/register');
  }

  //
  const handleTabSelect = (tab: TabType) => {
    setSelectedTab(tab);
  };

  // 선택된 탭에 따라 해당 컴포넌트 렌더링
  const renderSelectedComponent = () => {
    const reservations = hostReservations.map((reservation) => reservation.rentPeriod);

    switch (selectedTab) {
      case 'calendar':
        return (
          <div className="rounded-xl bg-white p-8">
            <div>
              <HostCalendar size="large" availableDates={availableDates} onAvailableDatesChange={setAvailableDates} reservations={reservations} />
            </div>
          </div>
        );
      case 'reservation':
        return ReservationCard; // TODO: reservations를 이용한 동적 컴포넌트로 변경
      default:
        return null;
    }
  };
  const reservations = hostReservations.sort((a, b) => {
    const dateA = a.rentPeriod[0];
    const dateB = b.rentPeriod[0];
    if (a.rentStatus && b.rentStatus) {
      const statusOrder = ['ready', 'using', 'cancel', 'terminated'];
      const statusIndexA = statusOrder.indexOf(a.rentStatus);
      const statusIndexB = statusOrder.indexOf(b.rentStatus);
      if (statusIndexA < statusIndexB) {
        return -1;
      } else if (statusIndexA > statusIndexB) {
        return 1;
      } else if (dateA && dateB) {
        if (dateA < dateB) {
          return 1;
        } else if (dateA > dateB) {
          return -1;
        }
      } else return 0;
    }
    return 0;
  });
  const ReservationCard = reservations.map((reservation, index) => (
        <ListComponent
          key={index}
          target={{
            type: 'host',
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


  return (
    <div className="flex flex-col gap-8 min-w-[768px]">
      <h2 className="mt-4 pl-3 text-3xl font-semibold">수현님, 등록한 차량을 관리해보세요!</h2>
      <div className="mb-10 flex gap-8">
        {/* Car Info Manage */}
        <div className="flex flex-col w-2/5 gap-3">
          <h3 className="pl-3 text-lg xl:text-xl font-medium">차량 정보</h3>
          <div className="flex flex-col w-full bg-white rounded-3xl shadow-xl overflow-hidden">
            {/* Image Gallery */}
            <ImageGallery imageUrls={carDetail.imageUrls} className='h-[300px] w-full rounded-xl' />

            <div className="flex flex-col justify-center gap-4 p-6">
              {/* Header : CarName, CarNumber, type, mileage, fuel */}
              <div className="flex flex-col gap-2">
                <div className="flex justify-between">
                  <div className="flex items-center gap-3">
                    <h1 className="text-base lg:text-2xl font-bold">{carDetail.categoryName}</h1>
                    <div className="text-background-700 font-semibold text-base">{carDetail.carNumber}</div>
                    {/* <Tag className="h-6 text-background-700 font-semibold text-base" text={data.car.carNumber} /> */}
                  </div>
                  <div className="flex gap-3">
                    <Button className="h-8 text-xs xl:text-sm whitespace-nowrap" text="수정" onClick={editCar}></Button>
                    <Button className="h-8 text-xs xl:text-sm whitespace-nowrap" text="삭제" type="danger" onClick={deleteCar}></Button>
                  </div>
                </div>
                <p className="text-background-500">
                  차종 {carDetail.type} &#183; 연비 {carDetail.mileage.toString()}km/L &#183; 연료 {carDetail.fuel}
                </p>
              </div>
              {/* Line */}
              <div className="h-0 w-full border-[0.5px] border-background-300"></div>
              {/* Car Info Detail */}
              <div className="flex flex-col gap-3">
                {/* carNumber */}
                <div className="flex gap-4 items-center">
                  <div className="w-8 h-8 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/location.svg" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">위치</p>
                    <p className="text-background-500 text-sm">{carDetail.address}</p>
                  </div>
                </div>

                {/* Year */}
                <div className="flex gap-4 items-center">
                  <div className="w-8 h-8 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/price.svg" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">가격</p>
                    <p className="text-background-500 text-sm">{carDetail.feePerHour?.toString()}원 / 시간</p>
                  </div>
                </div>

                {/* Address */}
                <div className="flex gap-4 items-center">
                  <div className="w-8 h-8 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/year.svg" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">연식</p>
                    <p className="text-background-500 text-sm">{carDetail.year?.toString()}년</p>
                  </div>
                </div>
              </div>

              {/* Line */}
              <div className="h-0 w-full border-[0.5px] border-background-300"></div>

              {/* Description */}
              <div className="px-2">
                <p className="whitespace-pre-line leading-8 text-background-500">{carDetail.description}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Calendar + Reservation */}
        <div className="flex w-3/5 flex-col gap-3">
          {/* Button div */}
          <div className="flex gap-6">
            <button
              className={`bg-gray-200 w-fit text-lg xl:text-xl whitespace-nowrap ${
                selectedTab === 'calendar' ? 'text-black font-bold' : 'text-background-300'
              } pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('calendar')}>
              예약 가능 일자
            </button>
            <button
              className={`bg-gray-200 w-fit text-lg xl:text-xl whitespace-nowrap ${
                selectedTab === 'reservation' ? 'text-black font-bold' : 'text-background-300'
              } pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('reservation')}>
              내 차 예약 내역
            </button>
          </div>
          <div className="flex flex-col max-h-screen gap-4 overflow-y-auto pr-6">{renderSelectedComponent()}</div>
        </div>
      </div>
    </div>
  );
}

export default HostManage;

