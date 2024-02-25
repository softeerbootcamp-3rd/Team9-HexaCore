import Button from '@/components/Button';
import { Link, useLocation } from 'react-router-dom';

type PaymentProps = {
  message: string;
  btnMessage: string;
  isSuccess: boolean;
};

function Payment({ message, isSuccess, btnMessage }: PaymentProps) {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const carId = params.get('carId');
  const startDate = params.get('startDate');
  const endDate = params.get('endDate');
  const rentTime = params.get('rentTime');
  const returnTime = params.get('returnTime');

  return (
    <div className='m-auto mt-10 max-w-sm rounded-2xl border border-background-200 bg-white p-6 shadow'>
      <div className='text-center text-lg font-bold'>{message}</div>
      {isSuccess ? (
        <div className='flex justify-center p-4'>
          <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='currentColor' className='text-info-400 aspect-square w-28'>
            <path
              fill-rule='evenodd'
              d='M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z'
              clip-rule='evenodd'
            />
          </svg>
        </div>
      ) : (
        <div className='flex justify-center p-4'>
          <svg
            xmlns='http://www.w3.org/2000/svg'
            fill='none'
            viewBox='0 0 24 24'
            strokeWidth='1.5'
            stroke='currentColor'
            className='aspect-square w-28 text-danger-500'>
            <path
              strokeLinecap='round'
              strokeLinejoin='round'
              d='M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z'
            />
          </svg>
        </div>
      )}

      <div className='flex w-full justify-center space-x-4'>
        <Link to={`/cars/${carId}?startDate=${startDate}&endDate=${endDate}&rentTime=${rentTime}&returnTime=${returnTime}`}>
          <Button text={btnMessage} className='block w-full px-4 py-2 text-center font-semibold' isRounded></Button>
        </Link>
      </div>
    </div>
  );
}

export default Payment;

