import { useState, useEffect } from 'react';

type ToastProps = {
  title: string;
  message: string;
  duration?: number;
};

function Toast({ title, message, duration = 2000 }: ToastProps) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
    const timer = setTimeout(() => {
      setIsVisible(false);
    }, duration);

    return () => clearTimeout(timer);
  }, [message, duration]);

  return isVisible && title && message ? (
    <div className={`fixed bottom-0 mb-4 flex w-full justify-center transition-opacity duration-300 ${isVisible ? 'opacity-100' : 'opacity-0'}`}>
      <div className='rounded-lg bg-danger-500 px-4 py-2 text-white shadow'>
        <div className='flex'>
          <div className='flex flex-col justify-center text-lg font-bold'>{title}</div>
          <button
            onClick={() => setIsVisible(false)}
            type='button'
            className='ms-auto inline-flex h-8 w-8 items-center justify-center rounded-lg bg-transparent p-1 text-sm text-white'
            data-modal-hide='default-modal'>
            <svg className='h-3 w-3' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 14 14'>
              <path stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6' />
            </svg>
          </button>
        </div>

        <div>{message}</div>
      </div>
    </div>
  ) : null;
}

export const useCustomToast = () => {
  const [message, setMessage] = useState('');
  const [title, setTitle] = useState('');
  const [key, setKey] = useState(0);
  const [duration, setDuration] = useState<number | undefined>(2000);

  const showToast = (title: string, newMessage: string, duration?: number) => {
    setTitle(title);
    setMessage(newMessage);
    setKey((prevKey) => prevKey + 1);
    setDuration(duration);
  };

  return {
    ToastComponent: () => <Toast key={key} title={title} message={message} duration={duration} />,
    showToast,
  };
};

