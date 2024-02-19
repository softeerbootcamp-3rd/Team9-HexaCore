import { Navigate, useLoaderData, useLocation } from 'react-router-dom';
import { CarData } from './carRoutes';
import { useEffect, useState } from 'react';
import Tag from '@/components/Tag';
import Button from '@/components/Button';
import TimePicker from '@/components/TimePicker';
import GuestCalendar from '@/components/calendar/guestCalendar/GuestCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { dateTimeToString, dateToString, stringToDate, stringTupleToDateRange } from '@/utils/converters';
import ImageGallery from '@/components/ImageGallery';
import { fetchUserCustomerKey } from '@/fetches/users/fetchUser';
import { createPortal } from 'react-dom';
import Payment from './Payment';

export type ReservationData = {
  carId: number;
  rentDateTime: string;
  returnDateTime: string;
};

function CarDetail() {
  const data = useLoaderData() as CarData;

  const [rentTime, setRentTime] = useState(9);
  const [returnTime, setReturnTime] = useState(9);

  const [totalFee, setTotalFee] = useState(0);

  const location = useLocation();
  const params = new URLSearchParams(location.search);

  const startDate = params.get('startDate') || dateToString(new Date());
  const endDate = params.get('endDate') || dateToString(new Date());

  const [dateRange, setDateRange] = useState<DateRange>(stringTupleToDateRange([startDate, endDate]));

  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [reservationData, setReservationData] = useState<ReservationData | null>(null);
  const [customerKey, setCustomerKey] = useState<string | null>(null);
  const [userName, setUserName] = useState<string | null>(null);

  // 가격 계산 함수
  const calculatePrice = (dateRange: DateRange) => {
    const startDate = new Date(dateRange[0]);
    const endDate = new Date(dateRange[1]);

    // 시간 정보 업데이트
    startDate.setHours(rentTime);
    endDate.setHours(returnTime);

    // 시간 간격 계산 (밀리초 단위)
    const timeDiff = endDate.getTime() - startDate.getTime();

    // 시간 간격을 시간 단위로 변환
    const hourInterval = timeDiff / (1000 * 60 * 60);

    // 시간당 요금과 시간 간격을 곱하여 총 요금 계산
    const calculatedFee = data.feePerHour * hourInterval;

    // 총 요금 업데이트
    setTotalFee(calculatedFee);
  };

  // 대여 시간 정보 업데이트
  const updateStartDateTime = (time: number) => {
    const startDateTime = new Date(dateRange[0]);
    startDateTime.setHours(time);
    const endDateTime = new Date(dateRange[1]);
    endDateTime.setHours(returnTime);

    if (startDateTime > endDateTime) {
      setReturnTime(time);
    }

    setRentTime(time);
  };

  // 반납 시간 정보 업데이트
  const updateEndDateTime = (time: number) => {
    const startDateTime = new Date(dateRange[0]);
    startDateTime.setHours(rentTime);
    const endDateTime = new Date(dateRange[1]);
    endDateTime.setHours(time);

    if (startDateTime > endDateTime) {
      setRentTime(time);
    }

    setReturnTime(time);
  };

  // 예약 날짜 정보 업데이트
  const updateDateRange = (dateRange: DateRange) => {
    setDateRange(dateRange);
  };

  // TimePicker 값 변경 시 가격 재계산
  useEffect(() => {
    calculatePrice(dateRange);
  }, [updateStartDateTime, updateEndDateTime]);

  if (data === null) {
    return <Navigate to='..' />;
  }

  // 예약하기 버튼 클릭 핸들러
  const handleReservation = async () => {
    const startDate = new Date(dateRange[0]);
    const endDate = new Date(dateRange[1]);

    // 시간 정보 업데이트
    startDate.setHours(rentTime);
    endDate.setHours(returnTime);

    // date를 string으로 변환
    const rentDate = dateTimeToString(startDate);
    const returnDate = dateTimeToString(endDate);

    const reservationData = {
      carId: data.carId,
      rentDateTime: rentDate,
      returnDateTime: returnDate,
    };
    setReservationData(reservationData);

    // 결제 모달창 호출
    const { customerKey, name } = await fetchUserCustomerKey();
    setCustomerKey(customerKey);
    setUserName(name);
    setIsOpen(true);
  };

  return (
    <div className='flex flex-col gap-8 px-40'>
      {/* Image Gallery */}
      <ImageGallery imageUrls={data.imageUrls} className='mx-10 h-[500px] rounded-xl' />
      {/* Car Detail + Reservation */}
      <div className='mx-10 mb-10 flex gap-9'>
        {/* Car Detail */}
        <div className='flex w-3/5 flex-col gap-4 rounded-3xl bg-white p-6 shadow-xl'>
          {/* Header : CarName, capacity(tag), type, mileage, fuel */}
          <div className=''>
            <div className='mb-2 flex items-center'>
              <h1 className='pr-4 text-xl font-bold'>{data.categoryName}</h1>
              <Tag className='h-6' text={`${data.capacity}인승`} />
            </div>
            <p className='text-background-500'>
              차종 {data.type} &#183; 연비 {data.mileage}km/L &#183; 연료 {data.fuel}
            </p>
          </div>

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Host Info */}
          <div className='flex items-center gap-4'>
            <div className='bg-gray-300 flex h-10 w-10 items-center justify-center overflow-hidden rounded-full'>
              <img src={data.host.profileImg || '/default-profile.svg'} alt='host-profile' />
            </div>
            <div className='flex flex-col'>
              <p className='font-semibold'>호스트</p>
              <p className='text-background-500'>{data.host.name}님</p>
            </div>
          </div>

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Car Info year, carAddress */}
          <div className='flex flex-col gap-3'>
            {/* carNumber */}
            <div className='flex items-center gap-4'>
              <div className='flex h-10 w-10 items-center justify-center overflow-hidden rounded-full pl-3'>
                <img src='/truck.svg' alt='car-number' />
              </div>
              <div className='flex flex-col'>
                <p className='font-semibold'>차량 번호</p>
                <p className='text-sm text-background-500'>{data.carNumber}</p>
              </div>
            </div>

            {/* Year */}
            <div className='flex items-center gap-4'>
              <div className='flex h-10 w-10 items-center justify-center overflow-hidden rounded-full pl-3'>
                <img src='/year.svg' alt='year' />
              </div>
              <div className='flex flex-col'>
                <p className='font-semibold'>연식</p>
                <p className='text-sm text-background-500'>{data.year}년</p>
              </div>
            </div>

            {/* Address */}
            <div className='flex items-center gap-4'>
              <div className='flex h-10 w-10 items-center justify-center overflow-hidden rounded-full pl-3'>
                <img src='/location.svg' alt='car-address' />
              </div>
              <div className='flex flex-col'>
                <p className='font-semibold'>픽업 위치</p>
                <p className='text-sm text-background-500'>{data.address}</p>
              </div>
            </div>
          </div>

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Description */}
          <div className=''>
            <p className='whitespace-pre-line leading-8 text-background-500'>{data.description}</p>
          </div>
        </div>

        {/* Reservation */}
        <div className='flex h-fit w-2/5 flex-col justify-center gap-4 rounded-3xl bg-white p-6 shadow-xl'>
          {/* Total Fee */}
          <p className='text-gray-900 text-3xl tracking-tight'>₩ {totalFee}</p>
          <div>
            <GuestCalendar 
              availableDates={data.carDateRanges}
              onReservationChange={updateDateRange}
              initDate={stringToDate(startDate)}
              reservation={dateRange}/>
          </div>
          {/* Date + Time Info */}
          <div className='grid grid-cols-2 gap-0 overflow-hidden rounded-xl border-[1px] border-background-300'>
            <label className='flex flex-col gap-1 border-b-[0.5px] border-r-[0.5px] border-background-300 p-3' htmlFor='rentHourSelect'>
              <p className='text-xs font-medium'>대여일</p>
              <p className='text-background-500'>
                {dateToString(dateRange[0]) === dateToString(new Date(0)) ? '' : dateToString(dateRange[0])}
              </p>
            </label>
            <label className='flex flex-col gap-1 border-b-[0.5px] border-l-[0.5px] border-background-300 p-3' htmlFor='rentHourSelect'>
              <p className='text-xs font-medium'>반납일</p>
              <p className='text-background-500'>
                {dateToString(dateRange[1]) === dateToString(new Date(0)) ? '' : dateToString(dateRange[1])}
              </p>
            </label>
            <div className='gap-1 border-r-[0.5px] border-t-[0.5px] border-background-300 p-3'>
              <p className='text-xs font-medium'>대여 시각</p>
              <TimePicker
                className='w-full text-background-500'
                id={'rentHourSelect'}
                onTimeChange={updateStartDateTime}
                time={rentTime.toString().padStart(2, '0')}
              />
            </div>
            <div className='gap-1 border-l-[0.5px] border-t-[0.5px] border-background-300 p-3'>
              <p className='text-xs font-medium'>반납 시각</p>
              <TimePicker
                className='w-full text-background-500'
                id={'returnHourSelect'}
                onTimeChange={updateEndDateTime}
                time={returnTime.toString().padStart(2, '0')}
              />
            </div>
          </div>

          {/* Reservation Button */}
          <Button text='예약하기' type='enabled' className='h-[45px] w-full' onClick={handleReservation} />
          {isOpen &&
            reservationData &&
            customerKey &&
            userName &&
            createPortal(
              <Payment
                price={totalFee}
                customerKey={customerKey}
                userName={userName}
                orderName={`${data.carNumber}:${reservationData.rentDateTime}-${reservationData.returnDateTime}`}
                reservationData={reservationData}
                onClose={() => setIsOpen(false)}></Payment>,
              document.body,
            )}
        </div>
      </div>
    </div>
  );
}

export default CarDetail;

