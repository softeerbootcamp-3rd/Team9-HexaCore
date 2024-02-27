import Button from '@/components/Button';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import { server } from '@/fetches/common/axios';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { CreateReservationData } from '@/fetches/reservations/Reservation.type';

type PaymentModalProps = {
  price: number;
  orderName: string;
  userName: string;
  reservationData: CreateReservationData;
  carNumber: string;
  onClose: () => void;
};

function PaymentModal({ onClose, reservationData, price, orderName, userName, carNumber }: PaymentModalProps) {
  const navigate = useNavigate();
  const [isPending, setIsPending] = useState<boolean>(false);
  const [isFailed, setIsFailed] = useState<boolean>(false);

  const BackDrop = () => {
    return <div className='fixed left-0 top-0 z-20 h-full w-full bg-background-400 bg-opacity-40'></div>;
  };

  const handlePayment = async () => {
    setIsPending(true);
    // 예약 결제 요청
    const response = await server.post<ResponseWithoutData>(`/reservations?orderName=${orderName}&userName=${userName}`, {
      data: reservationData,
    });

    if (!response || !response.success) {
      // 예약 실패
      setIsFailed(true);
      return;
    }
    // 예약 성공 시 모달 창 닫고 마이페이지로 리다이렉트
    setIsPending(false);
    onClose();
    navigate('/profile');
  };

  const formatDate = (dateString: string) => {
    const dates = dateString.split('T');
    const time = dates[1].split(':');
    return `${dates[0]} ${time[0]}:${time[1]}`;
  };

  return (
    <>
      <BackDrop />
      <figure className='fixed bottom-0 left-0 right-0 top-0 z-20 flex items-center justify-center'>
        <div className='relative flex min-h-[300px] min-w-[470px] flex-col justify-center rounded-2xl border border-background-200 bg-white p-8 shadow-lg'>
          {/* Payment Modal Header */}

          <div className='absolute right-0 top-0 flex h-12 w-full items-center justify-end p-3'>
            <button
              onClick={onClose}
              type='button'
              className='ms-auto inline-flex aspect-square w-10 items-center justify-center rounded-lg bg-transparent p-1 text-sm text-background-400'
              data-modal-hide='default-modal'>
              <svg className='mt-5 h-4 w-4' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 14 14'>
                <path stroke='currentColor' strokeLinecap='round' strokeLinejoin='round' strokeWidth='1' d='m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6' />
              </svg>
              <span className='sr-only'>Close modal</span>
            </button>
          </div>

          {/* Payment Modal Contents */}
          {isPending ? (
            !isFailed ? (
              <div className='ali flex flex-col justify-center gap-4'>
                <div className='mb-2 flex justify-center text-xl font-bold'>결제 진행 중...</div>
                <div className='flex justify-center'>이 화면을 벗어나지 마세요!</div>
                <div className='flex justify-center'>
                  <div className='flex h-20 w-20 animate-spin justify-center rounded-full border-8 border-background-200 border-t-primary-400' />
                </div>
              </div>
            ) : (
              <div className='ali flex flex-col justify-center gap-4'>
                <div className='mb-2 flex justify-center text-xl font-bold'>결제 실패</div>
                <div className='flex justify-center'>결제가 실패했습니다. 다시 시도해주세요. </div>
                <div className='flex justify-center'>
                  <div className='inline-flex aspect-square w-1/4 flex-shrink-0 items-center justify-center rounded-lg bg-danger-100 text-danger-500'>
                    <svg className='aspect-square w-1/2' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='currentColor' viewBox='0 0 20 20'>
                      <path d='M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM10 15a1 1 0 1 1 0-2 1 1 0 0 1 0 2Zm1-4a1 1 0 0 1-2 0V6a1 1 0 0 1 2 0v5Z' />
                    </svg>
                  </div>
                </div>
              </div>
            )
          ) : (
            <>
              <div className='ali flex flex-col justify-center gap-4'>
                <div className='mb-2 text-xl font-bold'>예약 확인</div>
                <div className='text-lg'>
                  <span className='font-semibold'>차량 번호:</span> <span>{carNumber}</span>
                </div>
                <div className='mb-2 text-md text-background-700'>
                  <span className='font-semibold'> 예약 날짜:</span> {formatDate(reservationData.rentDateTime)} ~ {formatDate(reservationData.returnDateTime)}
                </div>
                <div className='mb-2 text-md text-background-700'>
                  <span className='font-semibold'> 결제 금액: </span>
                  {price.toLocaleString()} 원
                </div>
              </div>
              <Button text='결제하기' className='mt-6 h-11' onClick={handlePayment} />
            </>
          )}
        </div>
      </figure>
    </>
  );
}

export default PaymentModal;

