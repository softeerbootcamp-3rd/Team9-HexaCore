import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import { CarData } from './hostsRoutes';
import { Reservation } from '@/types/Reservations';
import Button from '@/components/Button';
import ListComponent from '@/components/ListComponent';

function HostManage() {
  const navigate = useNavigate();
  const data = useLoaderData() as { car: CarData; reservations: Reservation[] };
  const [currentIdx, setCurrentIdx] = useState(0);

  const TABS = ['calendar', 'reservation'] as const;
  type TabType = (typeof TABS)[number];

  const [selectedTab, setSelectedTab] = useState<TabType>('calendar');

  // 다음 이미지 표시
  const showNextImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx + 1) % (data.car.imageUrls?.length ?? 1));
  };

  // 이전 이미지 표시
  const showPrevImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx - 1 + (data.car.imageUrls?.length ?? 1)) % (data.car.imageUrls?.length ?? 1));
  };

  const editCar = () => {
    alert('수정이 완료되었습니다.');
    location.reload();
  };

  const deleteCar = () => {
    alert('삭제가 완료되었습니다.');
    location.reload();
  };

  if (data.car === null) {
    navigate('/hosts/register');
  }

  //
  const handleTabSelect = (tab: TabType) => {
    setSelectedTab(tab);
  };

  // 선택된 탭에 따라 해당 컴포넌트 렌더링
  const renderSelectedComponent = () => {
    switch (selectedTab) {
      case 'calendar':
        return Calendar;
      case 'reservation':
        return ReservationCard;
      default:
        return Calendar;
    }
  };

  const Calendar = <div className="flex h-full justify-center items-center bg-white rounded-3xl shadow-xl">달력</div>;
  const ReservationCard = data.reservations
    ? data.reservations.map((reservation, index) => (
        <ListComponent
          key={index}
          target={{
            type: 'host',
            name: reservation.target.name ?? '',
            phoneNumber: reservation.target.phoneNumber ?? '',
            image: reservation.target.image ?? '',
          }}
          reservation={{
            startDate: reservation.startDate ?? '',
            endDate: reservation.endDate ?? '',
            status: reservation.status ?? '',
            price: reservation.fee ?? undefined,
            address: reservation.address ?? '',
          }}
        />
      ))
    : null;

  return (
    <div className="flex flex-col gap-8">
      <h2 className="mt-4 pl-3 text-3xl font-semibold">수현님, 등록한 차량을 관리해보세요!</h2>
      <div className="flex gap-8 mb-10">
        {/* Car Info Manage */}
        <div className="flex flex-col w-2/5 gap-3">
          <h3 className="pl-3 text-[22px] font-medium">차량 정보</h3>
          <div className="flex flex-col w-full bg-white rounded-3xl shadow-xl overflow-hidden">
            {/* Image Gallery */}
            <div className="flex w-full h-[300px] overflow-hidden relative">
              <button onClick={showPrevImage} className="absolute top-1/2 left-3 transform -translate-y-1/2 hover:brightness-75">
                <img src="/prev-button.svg" alt="Prev Button Image" />
              </button>
              <img className="w-full object-cover" src={data.car.imageUrls?.[currentIdx]} alt="car-img" />
              <button onClick={showNextImage} className="absolute top-1/2 right-3 transform -translate-y-1/2 hover:brightness-75">
                <img src="/next-button.svg" alt="Next Button Image" />
              </button>
            </div>

            <div className="flex flex-col justify-center p-6 gap-4">
              {/* Header : CarName, CarNumber, type, mileage, fuel */}
              <div className="flex flex-col gap-2">
                <div className="flex justify-between">
                  <div className="flex items-center gap-3">
                    <h1 className="text-2xl font-bold">{data.car.name}</h1>
                    <div className="text-background-700 font-semibold text-base">{data.car.number}</div>
                    {/* <Tag className="h-6 text-background-700 font-semibold text-base" text={data.car.carNumber} /> */}
                  </div>
                  <div className="flex gap-3">
                    <Button className="h-8" text="수정" onClick={editCar}></Button>
                    <Button className="h-8" text="삭제" type="danger" onClick={deleteCar}></Button>
                  </div>
                </div>
                <p className="text-background-500">
                  차종 {data.car.type} &#183; 연비 {data.car.mileage?.toString()}km/l &#183; 연료 {data.car.fuel}
                </p>
              </div>
              {/* Line */}
              <div className="w-full h-0 border-[0.5px] border-background-300"></div>
              {/* Car Info Detail */}
              <div className="flex flex-col gap-3">
                {/* carNumber */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="../public/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">위치</p>
                    <p className="text-background-500 text-sm">{data.car.carAddress}</p>
                  </div>
                </div>

                {/* Year */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="../public/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">가격</p>
                    <p className="text-background-500 text-sm">{data.car.feePerHour?.toString()}원/ 시간</p>
                  </div>
                </div>

                {/* Address */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="../public/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">연식</p>
                    <p className="text-background-500 text-sm">{data.car.year?.toString()}년</p>
                  </div>
                </div>
              </div>

              {/* Line */}
              <div className="w-full h-0 border-[0.5px] border-background-300"></div>

              {/* Description */}
              <div className="px-2">
                <p className="whitespace-pre-line text-background-500 leading-8">{data.car.description}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Calendar + Reservation */}
        <div className="flex flex-col gap-3 w-3/5">
          {/* Button div */}
          <div className="flex gap-6">
            <button
              className={`bg-gray-200 ${selectedTab === 'calendar' ? 'text-black font-bold' : 'text-background-300'} pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('calendar')}>
              예약 가능 일자
            </button>
            <button
              className={`bg-gray-200 ${selectedTab === 'reservation' ? 'text-black font-bold' : 'text-background-300'} pl-3 text-[20px] font-medium`}
              onClick={() => handleTabSelect('reservation')}>
              내 차 예약 내역
            </button>
          </div>
          <div className="flex flex-col max-h-screen gap-3 overflow-y-auto">{renderSelectedComponent()}</div>
        </div>
      </div>
    </div>
  );
}

export default HostManage;
