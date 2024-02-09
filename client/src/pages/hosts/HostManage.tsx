import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';
import { CarData } from './hostsRoutes';
import Button from '@/components/Button';

function HostManage() {
  const navigate = useNavigate();
  const data = useLoaderData() as CarData;
  const [currentIdx, setCurrentIdx] = useState(0);
  const [selectedTab, setSelectedTab] = useState('calendar');

  // 다음 이미지 표시
  const showNextImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx + 1) % data.imageUrls.length);
  };

  // 이전 이미지 표시
  const showPrevImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx - 1 + data.imageUrls.length) % data.imageUrls.length);
  };

  const editCar = () => {
    alert('수정이 완료되었습니다.');
    location.reload();
  };

  const deleteCar = () => {
    alert('삭제가 완료되었습니다.');
    location.reload();
  };

  console.log(data);
  if (data === null) {
    navigate('/hosts/register');
  }

  // 
  const handleTabSelect = (tab: string) => {
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

  const Calendar = (
    <div className="flex h-full justify-center items-center bg-white rounded-3xl shadow-xl">달력</div>
  );

  const ReservationCard = (
    <div className="flex flex-col bg-white rounded-3xl p-4">
      <ul role="list" className="divide-y divide-gray-100">
        <li key="person.email" className="flex justify-between gap-x-6 py-5">
          <div className="flex min-w-0 gap-x-4">
            <img className="h-12 w-12 flex-none rounded-full bg-gray-50" src="/default-profile.png" alt="" />
            <div className="min-w-0 flex-auto">
              <p className="text-sm font-semibold leading-6 text-gray-900">name</p>
              <p className="mt-1 truncate text-xs leading-5 text-gray-500">email</p>
            </div>
          </div>
          <div className="hidden justify-center shrink-0 sm:flex sm:flex-col sm:items-end">
            <p className="text-sm leading-6 text-gray-900 mr-5">role</p>
          </div>
        </li>
      </ul>
    </div>
  );

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
              <img className="w-full object-cover" src={data.imageUrls[currentIdx]} alt="car-img" />
              <button onClick={showNextImage} className="absolute top-1/2 right-3 transform -translate-y-1/2 hover:brightness-75">
                <img src="/next-button.svg" alt="Next Button Image" />
              </button>
            </div>

            <div className="flex flex-col justify-center p-6 gap-4">
              {/* Header : CarName, CarNumber, type, mileage, fuel */}
              <div className="flex flex-col gap-2">
                <div className="flex justify-between">
                  <div className="flex items-center gap-3">
                    <h1 className="text-2xl font-bold">{data.carName}</h1>
                    <div className="text-background-700 font-semibold text-base">{data.carNumber}</div>
                    {/* <Tag className="h-6 text-background-700 font-semibold text-base" text={data.carNumber} /> */}
                  </div>
                  <div className="flex gap-3">
                    <Button className="h-8" text="수정" onClick={editCar}></Button>
                    <Button className="h-8" text="삭제" type="danger" onClick={deleteCar}></Button>
                  </div>
                </div>
                <p className="text-background-500">차종 {data.type} &#183; 연비 {data.mileage.toString()}km/l &#183; 연료 {data.fuel}</p>
              </div>
              {/* Line */}
              <div className="w-full h-0 border-[0.5px] border-background-300"></div>
              {/* Car Info Detail */}
              <div className="flex flex-col gap-3">
                {/* carNumber */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">위치</p>
                    <p className="text-background-500 text-sm">{data.carAddress}</p>
                  </div>
                </div>

                {/* Year */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">가격</p>
                    <p className="text-background-500 text-sm">₩{data.feePerHour.toString()} /시간</p>
                  </div>
                </div>

                {/* Address */}
                <div className="flex gap-4 items-center">
                  <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                    <img src="/default-profile.png" alt="host-profile" />
                  </div>
                  <div className="flex flex-col">
                    <p className="font-semibold">연식</p>
                    <p className="text-background-500 text-sm">{data.year.toString()}년</p>
                  </div>
                </div>
              </div>

              {/* Line */}
              <div className="w-full h-0 border-[0.5px] border-background-300"></div>

              {/* Description */}
              <div className="px-2">
                <p className="whitespace-pre-line text-background-500 leading-8">{data.description}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Calendar + Reservation */}
        <div className="flex flex-col gap-3 w-3/5">
          {/* Button div */}
          <div className="flex gap-6">
            <button className={`bg-gray-200 ${selectedTab === 'calendar' ? 'text-black font-bold' : 'text-background-500'} pl-3 text-[20px] font-medium`} onClick={() => handleTabSelect('calendar')}>예약 가능 일자</button>
            <button className={`bg-gray-200 ${selectedTab === 'reservation' ? 'text-black font-bold' : 'text-background-500'} pl-3 text-[20px] font-medium`} onClick={() => handleTabSelect('reservation')}>내 차 예약 내역</button>
          </div>
          {renderSelectedComponent()}
        </div>
      </div>
    </div>

  );
}

export default HostManage;
