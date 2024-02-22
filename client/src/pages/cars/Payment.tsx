import { useEffect, useRef, useState } from 'react';
import { PaymentWidgetInstance, loadPaymentWidget } from '@tosspayments/payment-widget-sdk';
import Button from '@/components/Button';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import { useNavigate } from 'react-router-dom';
import { useCustomToast } from '@/components/Toast';
import { ReservationData } from '@/fetches/reservations/Reservation.type';

type PaymentProps = {
  price: number;
  customerKey: string;
  userName: string;
  orderName: string;
  reservationData: ReservationData;
  onClose: () => void;
};

function Payment({ price, customerKey, userName, orderName, reservationData, onClose }: PaymentProps) {
  const navigate = useNavigate();
  const paymentWidgetRef = useRef<PaymentWidgetInstance | null>(null);
  const paymentMethodsWidgetRef = useRef<ReturnType<PaymentWidgetInstance['renderPaymentMethods']> | null>(null);
  const clientKey = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;
  const [tossAgreed, setTossAgreed] = useState(false);
  const [isPending, setIsPending] = useState<boolean>(false);
  const [isFailed, setIsFailed] = useState<boolean>(false);
  const { ToastComponent, showToast } = useCustomToast();

  useEffect(() => {
    const initializeWidget = async () => {
      const paymentWidget = await loadPaymentWidget(clientKey, customerKey);

      const paymentMethodsWidget = paymentWidget.renderPaymentMethods('#payment-widget', { value: price ?? 0, currency: 'KRW', country: 'KR' });

      const paymentAgreement = paymentWidget.renderAgreement('#agreement');
      paymentAgreement.on('change', (agreementStatus) => {
        setTossAgreed(agreementStatus.agreedRequiredTerms);
      });
      paymentWidgetRef.current = paymentWidget;
      paymentMethodsWidgetRef.current = paymentMethodsWidget;
    };

    initializeWidget();
  }, []);

  const clickPayment = async () => {
    const paymentWidget = paymentWidgetRef.current;

    // 결제 요청
    await paymentWidget
      ?.requestPayment({
        orderId: crypto.randomUUID(),
        orderName: orderName,
        customerName: userName,
      })
      .then(async (data) => {
        setIsPending(true);
        // 성공 처리: 서버에 결제 승인 API 호출
        const response = await server.post<ResponseWithoutData>(
          `/reservations?paymentKey=${data?.paymentKey}&orderId=${data?.orderId}&amount=${data?.amount}`,
          {
            data: reservationData,
          },
        );

        if (!response || !response.success) {
          // 승인 실패
          setIsFailed(true);
          return;
        }
        setIsPending(false);
        // 성공 시 모달 창 닫고 마이페이지로 리다이렉트
        onClose();
        navigate('/profile');
      })
      .catch(() => {
        showToast('결제 실패', '결제가 실패했습니다.');
      });
  };

  const BackDrop = () => {
    return <div className='fixed left-0 top-0 z-20 h-full w-full bg-background-400 bg-opacity-40'></div>;
  };

  return (
    <>
      <BackDrop />
      <figure className='fixed bottom-0 left-0 right-0 top-0 z-20 flex items-center justify-center'>
        <div className='relative flex min-h-[600px] w-[500px] flex-col justify-center rounded-lg bg-white shadow-lg'>
          {/* Payment Modal Header */}
          <div className='absolute right-0 top-0 flex h-12 w-full items-center justify-end p-3'>
            <button
              onClick={onClose}
              type='button'
              className='ms-auto inline-flex aspect-square w-10 items-center justify-center rounded-lg bg-transparent p-1 text-sm text-background-400'
              data-modal-hide='default-modal'>
              <svg className='h-3 w-3' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 14 14'>
                <path stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6' />
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
              <div id='payment-widget' className='w-full p-4 pt-12' />
              <div id='agreement' style={{ width: '100%' }} />
              <div className='p-6 text-lg'>
                <span className='font-semibold'>결제 금액:</span> <span className='text-background-600'>{price} 원</span>
              </div>
              <Button text='결제하기' type={tossAgreed ? 'enabled' : 'disabled'} isRounded className='m-4 h-10' onClick={clickPayment} />
            </>
          )}
        </div>
        <ToastComponent />
      </figure>
    </>
  );
}

export default Payment;

