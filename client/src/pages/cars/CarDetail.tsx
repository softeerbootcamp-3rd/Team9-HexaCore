import { useLoaderData, useNavigate } from 'react-router-dom';
import { CarData } from './carRoutes';
import { useEffect, useState } from 'react';
import Tag from '@/components/Tag';
import Button from '@/components/Button';
import TimePicker from '@/components/TimePicker';

function CarDetailTest() {
  const navigator = useNavigate();
  const data = useLoaderData() as CarData;
  const [currentIdx, setCurrentIdx] = useState(0);
  const [totalFee, setTotalFee] = useState(0);

  const [startDateTime, setStartDateTime] = useState(new Date(2024, 1, 2, 12, 0, 0, 0));
  const [endDateTime, setEndDateTime] = useState(new Date(2024, 1, 3, 12, 0, 0, 0));

  // 다음 이미지 표시
  const showNextImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx + 1) % data.imageUrls.length);
  };

  // 이전 이미지 표시
  const showPrevImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx - 1 + data.imageUrls.length) % data.imageUrls.length);
  };

  // 대여 시간 정보 업데이트
  const updateStartDateTime = (time: string) => {
    setStartDateTime((prevDate: Date) => {
      const newDate = new Date(prevDate);
      newDate.setHours(parseInt(time));
      
      return newDate;
    });
  };

  // 반납 시간 정보 업데이트
  const updateEndDateTime = (time: string) => {
    setEndDateTime((prevDate: Date) => {
      const newDate = new Date(prevDate);
      newDate.setHours(parseInt(time));
      
      return newDate;
    });
  };

  // 가격 계산 함수
  const calculatePrice = () => {
    console.log("endDateTime: " + endDateTime);
    console.log("startDateTime: " + startDateTime);

    // 시간 간격 계산 (밀리초 단위)
    const timeDiff = endDateTime.getTime() - startDateTime.getTime();

    // 시간 간격을 시간 단위로 변환
    const hourInterval = timeDiff / (1000 * 60 * 60);

    // 시간당 요금과 시간 간격을 곱하여 총 요금 계산
    const calculatedFee = data.feePerHour * hourInterval;

    // 총 요금 업데이트
    setTotalFee(calculatedFee);
  };

  // TimePicker 값 변경 시 가격 재계산
  useEffect(() => {
    calculatePrice();
  }, [startDateTime, endDateTime]);

  // DateTime을 string으로 변환
  const dateTimeToString = (date: Date) => {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}T${date.getHours()}:00:00`;
  };

  // 예약하기 버튼 클릭 핸들러
  const handleReservation = () => {
    // date를 string으로 변환
    const rentDate = dateTimeToString(startDateTime);
    const returnDate = dateTimeToString(endDateTime);

    const reservationData = {
      carId: data.carId,
      rentDate: rentDate,
      returnDate: returnDate
    };
    console.log(reservationData);

    // 마이페이지로 이동
    navigator("/profile/1");
  };

  return (
    <div className="flex flex-col gap-8 px-40">
      {/* Image Gallery */}
      <div className="flex h-[500px] mx-10 rounded-lg overflow-hidden relative">
        <button onClick={showPrevImage} className="absolute top-1/2 left-5 transform -translate-y-1/2 hover:brightness-75">
          <img src="/prev-button.svg" alt="Prev Button Image" />
        </button>
        <img className="rounded-lg w-full object-cover" src={data.imageUrls[currentIdx]} alt="car-img" />
        <button onClick={showNextImage} className="absolute top-1/2 right-5 transform -translate-y-1/2 hover:brightness-75">
          <img src="/next-button.svg" alt="Next Button Image" />
        </button>
      </div>

      {/* Car Detail + Reservation */}
      <div className="flex gap-9 mx-10 mb-10">
        {/* Car Detail */}
        <div className="flex flex-col w-3/5 gap-4 bg-white rounded-3xl shadow-xl p-6">
          {/* Header : CarName, capacity(tag), type, mileage, fuel */}
          <div className="">
            <div className="flex items-center mb-2">
              <h1 className="text-xl font-bold pr-4">{data.carName}</h1>
              <Tag className="h-6" text={`${data.capacity}인승`} />
            </div>
            <p className="text-background-500">차종 {data.type} &#183; 연비 {data.mileage}km/l &#183; 연료 {data.fuel}</p>
          </div>

          {/* Line */}
          <div className="w-full h-0 border-[0.5px] border-background-300"></div>

          {/* Host Info */}
          <div className="flex gap-4 items-center">
            <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
              <img src={data.host.profileImg} alt="host-profile" />
            </div>
            <div className="flex flex-col">
              <p className="font-semibold">호스트</p>
              <p className="text-background-500">{data.host.name}님</p>
            </div>
          </div>

          {/* Line */}
          <div className="w-full h-0 border-[0.5px] border-background-300"></div>

          {/* Car Info year, carAddress */}
          <div className="flex flex-col gap-3">
            {/* carNumber */}
            <div className="flex gap-4 items-center">
              <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                <img src={data.host.profileImg} alt="host-profile" />
              </div>
              <div className="flex flex-col">
                <p className="font-semibold">차량 번호</p>
                <p className="text-background-500 text-sm">{data.carNumber}</p>
              </div>
            </div>

            {/* Year */}
            <div className="flex gap-4 items-center">
              <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                <img src={data.host.profileImg} alt="host-profile" />
              </div>
              <div className="flex flex-col">
                <p className="font-semibold">연식</p>
                <p className="text-background-500 text-sm">{data.year}년</p>
              </div>
            </div>

            {/* Address */}
            <div className="flex gap-4 items-center">
              <div className="w-14 h-14 rounded-full overflow-hidden bg-gray-300 flex items-center justify-center">
                <img src={data.host.profileImg} alt="host-profile" />
              </div>
              <div className="flex flex-col">
                <p className="font-semibold">픽업 위치</p>
                <p className="text-background-500 text-sm">{data.carAddress}</p>
              </div>
            </div>
          </div>

          {/* Line */}
          <div className="w-full h-0 border-[0.5px] border-background-300"></div>

          {/* Description */}
          <div className="">
            <p className="whitespace-pre-line text-background-500 leading-8">{data.description}</p>
          </div>
        </div>

        {/* Reservation */}
        <div className="flex flex-col h-fit w-2/5 rounded-3xl shadow-xl bg-white justify-center p-6 gap-4">
          {/* Total Fee */}
          <p className="text-3xl tracking-tight text-gray-900">
            ₩ {totalFee}
          </p>
          <div>캘린더</div>
          {/* Date + Time Info */}
          <div className="grid grid-cols-2 gap-0 rounded-xl border-[1px] border-background-300 overflow-hidden">
            <label className="flex flex-col border-b-[0.5px] border-r-[0.5px] border-background-300 p-3 gap-1" htmlFor="rentHourSelect">
              <p className="font-medium text-xs">대여일</p>
              <p className="text-background-500">{startDateTime.getFullYear()}-{(startDateTime.getMonth() + 1).toString().padStart(2, '0')}-{startDateTime.getDate().toString().padStart(2, '0')}</p>
            </label>
            <label className="flex flex-col border-b-[0.5px] border-l-[0.5px] border-background-300 p-3 gap-1" htmlFor="rentHourSelect">
              <p className="font-medium text-xs">반납일</p>
              <p className="text-background-500">{endDateTime.getFullYear()}-{(endDateTime.getMonth() + 1).toString().padStart(2, '0')}-{endDateTime.getDate().toString().padStart(2, '0')}</p>
            </label>
            <div className="border-t-[0.5px] border-r-[0.5px] border-background-300 p-3 gap-1">
              <p className="font-medium text-xs">대여 시각</p>
              <TimePicker className="text-background-500 w-full" id={"rentHourSelect"} onTimeChange={updateStartDateTime} />
            </div>
            <div className="border-t-[0.5px] border-l-[0.5px] border-background-300 p-3 gap-1">
              <p className="font-medium text-xs">반납 시각</p>
              <TimePicker className="text-background-500 w-full" id={"returnHourSelect"} onTimeChange={updateEndDateTime} />
            </div>
          </div>

          {/* Reservation Button */}
          <Button text="예약하기" type="enabled" className="w-full h-[45px]" onClick={handleReservation} />
        </div>
      </div>
    </div>
  );
}

export default CarDetailTest;
