import { RouteObject, redirect } from 'react-router-dom';
import PaymentIsPending from './PaymentIsPending';
import { patchUserBillingKey } from '@/fetches/users/fetchUser';
import Payment from './Payment';

const paymentRoutes: RouteObject[] = [
  {
    path: 'payment/pending',
    loader: async ({ request }) => {
      const { customerKey, authKey, carId, startDate, endDate, rentTime, returnTime } = parseQueryString(request.url);
      const isSuccess = await patchUserBillingKey(customerKey, authKey);
      if (isSuccess) {
        return redirect(`/payment/success?carId=${carId}&startDate=${startDate}&endDate=${endDate}&rentTime=${rentTime}&returnTime=${returnTime}`);
      }
      return redirect(`/payment/fail?carId=${carId}&startDate=${startDate}&endDate=${endDate}`);
    },
    element: <PaymentIsPending />,
  },
  {
    path: 'payment/success',
    element: <Payment isSuccess={true} message='카드 등록이 완료되었습니다' btnMessage='결제 계속하기' />,
  },
  {
    path: 'payment/fail',
    element: <Payment isSuccess={false} message='카드 등록이 실패했습니다' btnMessage='이전화면으로 돌아가기' />,
  },
];

const parseQueryString = (url: string) => {
  const params = new URLSearchParams(new URL(url).search);
  const customerKey = params.get('customerKey');
  const authKey = params.get('authKey');
  const carId = params.get('carId');
  const startDate = params.get('startDate');
  const endDate = params.get('endDate');
  const rentTime = params.get('rentTime');
  const returnTime = params.get('returnTime');
  return {
    customerKey: customerKey,
    authKey: authKey,
    carId: carId,
    startDate: startDate,
    endDate: endDate,
    rentTime,
    returnTime,
  };
};

export default paymentRoutes;

