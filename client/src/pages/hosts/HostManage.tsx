import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import Button from '@/components/Button';
import HostCalendar from '@/components/calendar/hostCalendar/HostCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { HostManageLoaderData } from './hostsRoutes';

const TABS = ['calendar', 'reservation'] as const;
type TabType = (typeof TABS)[number];

function HostManage() {
  const navigate = useNavigate();
  const { carDetail, hostReservations } = useLoaderData() as HostManageLoaderData;
  const [currentIdx, setCurrentIdx] = useState(0);
  const [selectedTab, setSelectedTab] = useState<TabType>('calendar');
  const [availableDates, setAvailableDates] = useState<DateRange[]>(carDetail.dates);

  // 다음 이미지 표시
  const showNextImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx + 1) % carDetail.imageUrls.length);
  };

  // 이전 이미지 표시
  const showPrevImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx - 1 + carDetail.imageUrls.length) % carDetail.imageUrls.length);
  };

  const editCar = () => {
    alert('수정이 완료되었습니다.');
    location.reload();
  };

  const deleteCar = () => {
    alert('삭제가 완료되었습니다.');
    location.reload();
  };

  console.log(carDetail);
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

  const ReservationCard = (
    <div className="flex flex-col rounded-3xl bg-white p-4">
      <ul role="list" className="divide-gray-100 divide-y">
        <li key="person.email" className="flex justify-between gap-x-6 py-5">
          <div className="flex min-w-0 gap-x-4">
            <img className="bg-gray-50 h-12 w-12 flex-none rounded-full" src="/default-profile.png" alt="" />
            <div className="min-w-0 flex-auto">
              <p className="text-gray-900 text-sm font-semibold leading-6">name</p>
              <p className="text-gray-500 mt-1 truncate text-xs leading-5">email</p>
            </div>
          </div>
          <div className="hidden shrink-0 justify-center sm:flex sm:flex-col sm:items-end">
            <p className="text-gray-900 mr-5 text-sm leading-6">role</p>
          </div>
        </li>
      </ul>
    </div>
  );

  return (
    <div className="flex flex-col gap-8">
      <h2 className="mt-4 pl-3 text-3xl font-semibold">수현님, 등록한 차량을 관리해보세요!</h2>
      <div className="mb-10 flex gap-8">
        {/* Car Info Manage */}
        <div className="flex w-2/5 flex-col gap-3">
          <h3 className="pl-3 text-[22px] font-medium">차량 정보</h3>
          <div className="flex w-full flex-col overflow-hidden rounded-3xl bg-white shadow-xl">
            {/* Image Gallery */}
            <div className="relative flex h-[300px] w-full overflow-hidden">
              <button onClick={showPrevImage} className="absolute left-3 top-1/2 -translate-y-1/2 transform hover:brightness-75">
                <img src="/prev-button.svg" alt="Prev Button Image" />
              </button>
              <img className="w-full object-cover" src={carDetail.imageUrls[currentIdx]} alt="car-img" />
              <button onClick={showNextImage} className="absolute right-3 top-1/2 -translate-y-1/2 transform hover:brightness-75">
                <img src="/next-button.svg" alt="Next Button Image" />
              </button>
            </div>

            <div className="flex flex-col justify-center gap-4 p-6">
              {/* Header : CarName, CarNumber, type, mileage, fuel */}
              <div className="flex flex-col gap-2">
                <div className="flex justify-between">
                  <div className="flex items-center gap-3">
                    <h1 className="text-2xl font-bold">{carDetail.carName}</h1>
                    <div className="text-base font-semibold text-background-700">{carDetail.carNumber}</div>
                    {/* <Tag className="h-6 text-background-700 font-semibold text-base" text={carDetail.carNumber} /> */}
                  </div>
                  <div className="flex gap-3">
                    <Button className="h-8" text="수정" onClick={editCar}></Button>
                    <Button className="h-8" text="삭제" type="danger" onClick={deleteCar}></Button>
                  </div>
                </div>
                <p className="text-background-500">
                  차종 {carDetail.type} &#183; 연비 {carDetail.mileage.toString()}km/l &#183; 연료 {carDetail.fuel}
                </p>
              </div>
              {/* Line */}
              <div className="h-0 w-full border-[0.5px] border-background-300"></div>
              {/* Car Info Detail */}
              <div className="flex flex-col gap-3">
                {/* carNumber */}
                <div className="flex items-center gap-4">
                  <div className="bg-gray-300 flex h-14 w-14 items-center justify-center overflow-hidden rounded-full">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">위치</p>
                    <p className="text-sm text-background-500">{carDetail.carAddress}</p>
                  </div>
                </div>

                {/* Year */}
                <div className="flex items-center gap-4">
                  <div className="bg-gray-300 flex h-14 w-14 items-center justify-center overflow-hidden rounded-full">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">가격</p>
                    <p className="text-sm text-background-500">₩{carDetail.feePerHour.toString()} /시간</p>
                  </div>
                </div>

                {/* Address */}
                <div className="flex items-center gap-4">
                  <div className="bg-gray-300 flex h-14 w-14 items-center justify-center overflow-hidden rounded-full">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">연식</p>
                    <p className="text-sm text-background-500">{carDetail.year.toString()}년</p>
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
              className={`bg-gray-200 ${selectedTab === 'calendar' ? 'font-bold text-black' : 'text-background-300'} pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('calendar')}>
              예약 가능 일자
            </button>
            <button
              className={`bg-gray-200 ${selectedTab === 'reservation' ? 'font-bold text-black' : 'text-background-300'} pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('reservation')}>
              내 차 예약 내역
            </button>
          </div>
          {renderSelectedComponent()}
        </div>
      </div>
    </div>
  );
}

export default HostManage;

