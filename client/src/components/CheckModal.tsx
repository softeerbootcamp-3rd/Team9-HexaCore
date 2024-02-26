import React from 'react';
import CloseIcon from './svgs/CloseIcon';

interface ModalProps {
  title: string;
  content: string;
  onCancel: () => void;
  confirmMsg: string;
  onConfirm: () => void;
}

const CheckModal: React.FC<ModalProps> = ({ title, content, onCancel, confirmMsg, onConfirm }) => {
  return (
      <div className='flex flex-row fixed left-0 top-0 z-20 h-full w-full bg-background-500 bg-opacity-40 items-center justify-center'>
        <div className='h-[190px] min-w-[410px] flex-col justify-center rounded-xl border border-background-200 bg-white p-8 shadow-lg'>

          <div className='flex justify-between items-baseline'>
            <h2 className='text-[18px] text-background-700 font-[600] mb-4'>
                {title}
            </h2>

            <button onClick={onCancel}>
              <CloseIcon />
            </button>
          </div>

          <p className='text-sm mb-8 text-background-700'>
            {content}
          </p>
          
          <div className='flex justify-end mt-8'>
            <button
              className='mr-5 px-4 py-2 min-w-20 text-sm text-white rounded-lg bg-background-400 hover:bg-background-500'
              onClick={onCancel}
            >
              취소
            </button>
            <button
              className=' px-4 py-2 min-w-20 text-sm text-white rounded-lg bg-danger-400 hover:bg-danger-500'
              onClick={onConfirm}
            >
              {confirmMsg}
            </button>
          </div>
        </div>
      </div>
  );
};

export default CheckModal;
