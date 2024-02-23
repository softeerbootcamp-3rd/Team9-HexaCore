import { useState, useEffect } from 'react';

type ToastProps = {
  title: string;
  message: string;
  info: boolean;
  duration?: number;
  isVisible: boolean;
  setIsVisible: (isVisible: boolean) => void;
};

function Toast({ title, message, info = false, duration = 2000, isVisible, setIsVisible }: ToastProps) {

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
    }, duration);

    return () => clearTimeout(timer);
  }, [message, duration]);

  return isVisible && title && message ? (
    <div className='relative'>
      <div className={`absolute bottom-0 left-1/2 -translate-x-1/2  mb-4 bg-primary-50 justify-center transition-opacity duration-300 ${isVisible ? 'opacity-100' : 'opacity-0'}`}>
        <div className={`flex gap-4 rounded-lg px-4 py-2 text-white shadow ${info ? 'bg-info-400' : 'bg-danger-500'}`}>
          <div className='flex flex-col'>
            <div className='justify-center text-base font-bold'>{title}</div>
            <div className='text-sm'>{message}</div>
          </div>
          <button
            onClick={() => setIsVisible(false)}
            type='button'
            className='inline-flex h-8 w-8 items-center justify-end rounded-lg bg-transparent text-sm text-white'
            data-modal-hide='default-modal'>
            <svg className='h-3 w-3' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 14 14'>
              <path stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6' />
            </svg>
          </button>
        </div>
      </div>
    </div>
  ) : null;
}

export const useCustomToast = () => {
  const [message, setMessage] = useState('');
  const [title, setTitle] = useState('');
  const [key, setKey] = useState(0);
  const [info, setInfo] = useState(false);
  const [duration, setDuration] = useState<number | undefined>(2000);
  const [isVisible, setIsVisible] = useState(false);

  const showToast = (title: string, newMessage: string, info?: boolean, duration?: number) => {
    setIsVisible(true);
    setTitle(title);
    setMessage(newMessage);
    setKey((prevKey) => prevKey + 1);
    setDuration(duration);
    info && setInfo(info);
  };

  return {
    ToastComponent: () => <Toast key={key} title={title} message={message} duration={duration} info={info} isVisible={isVisible} setIsVisible={setIsVisible} />,
    showToast,
  };
};

