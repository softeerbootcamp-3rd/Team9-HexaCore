import { Navigate, useLoaderData, useLocation } from 'react-router-dom';
import { CarDetailLoaderData } from './carRoutes';
import { useEffect, useRef, useState } from 'react';
import Tag from '@/components/Tag';
import Button from '@/components/Button';
import TimePicker from '@/components/TimePicker';
import GuestCalendar from '@/components/calendar/guestCalendar/GuestCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { dateTimeToString, formatDate, stringToDate, stringTupleToDateRange } from '@/utils/converters';
import ImageGallery from '@/components/ImageGallery';
import { fetchUserPaymentInfo } from '@/fetches/users/fetchUser';
import { createPortal } from 'react-dom';
import PaymentModal from '@/pages/cars/components/PaymentModal';
import { useCustomToast } from '@/components/Toast';
import StarIcon from '@/components/review/StarIcon';
import { CreateReservationData } from '@/fetches/reservations/Reservation.type';
import ImageInfo from './ImageInfo';
import { CarReviewResponse } from '@/fetches/reviews/Review.type';
import { fetchCarReviews } from '@/fetches/reviews/fetchReviews';
import Review from '@/components/review/Review';

function CarDetail() {
  const data = useLoaderData() as CarDetailLoaderData;
  const [rentTime, setRentTime] = useState(9);
  const [returnTime, setReturnTime] = useState(9);
  const [totalFee, setTotalFee] = useState(0);

  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const startDate = params.get('startDate') ?? formatDate(new Date());
  const endDate = params.get('endDate') ?? formatDate(new Date());
  const [dateRange, setDateRange] = useState<DateRange>(stringTupleToDateRange([startDate, endDate]));

  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [reservationData, setReservationData] = useState<CreateReservationData | null>(null);
  const [userName, setUserName] = useState<string>('');
  const clientKey = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;
  const { ToastComponent, showToast } = useCustomToast();

  const loaderRefNext = useRef(null);
  const [reviews, setReviews] = useState<CarReviewResponse[]>(data?.review ?? []);
  const [page, setPage] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(false);

  // 무한 스크롤 요청
  useEffect(() => {
    const fetchReviews = async () => {
      const response = await fetchCarReviews(data.carData.carId, page, 10, 'id,DESC');
      if(!response || !response.success) {
        showToast('리뷰 조회 실패', '리뷰를 불러오는데 실패하였습니다.');
        return;
      }

      setReviews((prevReviews) => [...prevReviews, ...response.data]);
      setHasNext(response.pageInfo.hasNext);
    };

    if(hasNext) {
      fetchReviews();
    }
  }, [page])

  useEffect(() => {
    setHasNext(data.hasNext);
    const observerNext = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          setPage((prevPage) => prevPage + 1);
        }
      },
      { threshold: 1.0, rootMargin: '0px 0px 10% 0px' },
    );

    if (loaderRefNext.current) {
      observerNext.observe(loaderRefNext.current);
    }

    return () => {
      if (loaderRefNext.current) {
        observerNext.unobserve(loaderRefNext.current);
      }
    };
  }, []);

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

  // TimePicker 값 변경 시 가격 재계산
  useEffect(() => {
    const startDate = new Date(dateRange[0]);
    const endDate = new Date(dateRange[1]);

    // 시간 정보 업데이트
    startDate.setHours(rentTime);
    endDate.setHours(returnTime);

    // 시간 간격 계산 (밀리초 단위)
    const timeDiff = endDate.getTime() - startDate.getTime();
    const hourInterval = timeDiff / (1000 * 60 * 60);

    // 시간당 요금과 시간 간격을 곱하여 총 요금 계산
    const calculatedFee = data.carData.feePerHour * hourInterval;

    // 총 요금 업데이트
    setTotalFee(calculatedFee);
  }, [rentTime, returnTime, dateRange, data.carData?.feePerHour]);

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

    // 만약 rentDate와 returnDate가 동일할 경우 예약 불가능
    if (rentDate === returnDate) {
      showToast('예약 실패', '최소 예약 단위는 1시간입니다.');
      return;
    }

    // 만약 returnDate가 현재 시각보다 이전일 경우 예약 불가능
    if(endDate < new Date()) {
      showToast('예약 실패', '현재 시각 이후 부터 대여 가능합니다.');
      return;
    }

    const reservationData = {
      carId: data.carData.carId,
      rentDateTime: rentDate,
      returnDateTime: returnDate,
    };
    setReservationData(reservationData);

    // 결제 모달창 호출
    const { customerKey, name, hasBillingKey } = await fetchUserPaymentInfo();
    setUserName(name);

    if (!customerKey) {
      // customerKey가 없으면 예약 실패 알람
      showToast('예약 실패', '예약을 진행할 수 없습니다.');
    } else if (!hasBillingKey) {
      // billingKey가 없으면 토스 billing 모달 띄우기
      await openTossModal(customerKey);
    } else {
      // customerKey, billingKey 모두 있으면 예약 확인 모달 띄우기
      setIsOpen(true);
    }
  };

  const openTossModal = async (customerKey: string) => {
    if (!customerKey) return;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-expect-error
    const tossPayments = window.TossPayments(clientKey);
    tossPayments.requestBillingAuth('카드', {
      customerKey: customerKey,
      successUrl: `${window.location.origin}/payment/pending?carId=${data.carData.carId}`,
      failUrl: `${window.location.origin}/payment/fail`,
    });
  };

  return (
    <div className='flex flex-col gap-8 px-40 mx-10 mb-10'>
      {/* Image Gallery */}
      <ImageGallery imageUrls={data.carData.imageUrls} className='h-[500px] rounded-xl' />
      {/* Car Detail + Reservation */}
      <div className='flex gap-9'>
        {/* Car Detail */}
        <div className='flex w-3/5 flex-col gap-4 rounded-3xl bg-white p-6 shadow-xl'>
          {/* Header : CarName, capacity(tag), type, mileage, fuel */}
          <div>
            <div className='mb-2 flex items-center justify-between'>
              <div className='flex gap-1'>
                <h1 className='text-xl font-bold'>{data.carData.categoryName}</h1>
                <div className='flex gap-1 items-center'>
                  <StarIcon filled={true} className='w-4 h-4' />
                  <div className='text-sm'>{data.carData.averageRate?.toFixed(1) ?? 0}</div>
                </div>
              </div>
              <Tag className='h-[30px] px-4' text={`${data.carData.capacity}인승`} />
            </div>
            <p className='text-background-500'>
              차종 {data.carData.type} &#183; 연비 {data.carData.mileage}km/L &#183; 연료 {data.carData.fuel}
            </p>
          </div>

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Host Info */}
          <ImageInfo img={data.carData.host.profileImg ?? '/defaultProfile.png'} alt='host-profile'
            title='호스트' info={`${data.carData.host.name}님`} />

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Car Info year, carAddress */}
          <div className='flex flex-col gap-3'>
            {/* carNumber */}
            <ImageInfo img='/truck.svg' alt='car-number'
              title='차량 번호' info={data.carData.carNumber} className='pl-2' />

            {/* Year */}
            <ImageInfo img='/year.svg' alt='year'
              title='연식' info={`${data.carData.year}년`} className='pl-2'/>

            {/* Address */}
            <ImageInfo img='/location.svg' alt='car-address'
              title='픽업 위치' info={data.carData.address} className='pl-2' />
          </div>

          {/* Line */}
          <div className='h-0 w-full border-[0.5px] border-background-300'></div>

          {/* Description */}
          <div className=''>
            <p className='whitespace-pre-line leading-8 text-background-500'>{data.carData.description}</p>
          </div>
        </div>

        {/* Reservation */}
        <div className='flex h-fit w-2/5 flex-col justify-center gap-4 rounded-3xl bg-white p-6 shadow-xl'>
          {/* Total Fee */}
          <p className='text-gray-900 text-3xl tracking-tight'>₩ {totalFee.toLocaleString()}</p>
          <div>
            <GuestCalendar
              availableDates={data.carData.carDateRanges}
              onReservationChange={setDateRange}
              initDate={stringToDate(startDate)}
              reservation={dateRange}
            />
          </div>
          {/* Date + Time Info */}
          <div className='grid grid-cols-2 gap-0 overflow-hidden rounded-xl border-[1px] border-background-300'>
            <label className='flex flex-col gap-1 border-b-[0.5px] border-r-[0.5px] border-background-300 p-3' htmlFor='rentHourSelect'>
              <p className='text-xs font-medium'>대여일</p>
              <p className='text-background-500 min-h-6'>
                {formatDate(dateRange[0]) === formatDate(new Date(0)) ? '' : formatDate(dateRange[0])}
              </p>
            </label>
            <label className='flex flex-col gap-1 border-b-[0.5px] border-l-[0.5px] border-background-300 p-3' htmlFor='rentHourSelect'>
              <p className='text-xs font-medium'>반납일</p>
              <p className='text-background-500 min-h-6'>
                {formatDate(dateRange[1]) === formatDate(new Date(0)) ? '' : formatDate(dateRange[1])}
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
          <Button text='예약하기' type='enabled' className='h-[45px] w-full' onClick={handleReservation} />
          {/* 결제 모달 창 */}
          {isOpen &&
            reservationData &&
            createPortal(
              <PaymentModal
                price={totalFee}
                userName={userName}
                orderName={`${data.carData.carNumber}:${reservationData.rentDateTime}-${reservationData.returnDateTime}`}
                reservationData={reservationData}
                onClose={() => setIsOpen(false)}
              />,
              document.body,
            )}
        </div>
      </div>
      {/* Review */}
      <div className='flex flex-col gap-4 rounded-3xl bg-white shadow-xl p-6 '>
        <p className='font-bold'>{reviews.length === 0 ? '아직 작성된 리뷰가 없습니다' : `후기 ${data.totalReviews}개`}</p>
          {reviews.length !== 0 &&
            <div className='grid grid-cols-2 gap-10'>
            {reviews.map((review, i) => (
              <Review
                key={i}
                imgUrl={review.writer.profileImgUrl}
                name={review.writer.name}
                rate={review.rate}
                contents={review.contents}
              />
            ))}
          </div>
          }
      </div>
      <div ref={loaderRefNext}></div>
      <ToastComponent />
    </div>
  );
}

export default CarDetail;
