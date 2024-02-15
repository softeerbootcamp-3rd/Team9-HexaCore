import { forwardRef, type HTMLInputTypeAttribute, ChangeEventHandler } from 'react';

type Props = {
  title?: string;
  placeHolder: string;
  type?: HTMLInputTypeAttribute;
  onChange?: ChangeEventHandler<HTMLInputElement>;
  isWrong?: boolean;
  className?: string;
  errorMsg?: string;
};

const InputBox = forwardRef<HTMLInputElement, Props>(({ title, placeHolder, type = 'text', onChange, isWrong = false, className, errorMsg }, ref) => {
  return (
    <div className='flex flex-col'>
      <div className="w-full pt-4 pb-2 flex">
        <div className="text-background-600 w-24 text-sm pl-5 flex flex-col justify-center">{title}</div>
        <input
          className={`
          ${isWrong ? 'ring-2 ring-danger-300' : 'focus:ring-2 focus:ring-primary-300'}
          focus:outline-none w-4/5 h-12 text-sm py-2 pl-5 shadow-md rounded-xl
          ${className}`}
          type={type}
          placeholder={placeHolder}
          ref={ref}
          onChange={onChange}
        />
      </div>

      <div className='text-danger-400 h-4 text-[12px] text-end text-top pr-5'>{errorMsg}</div>
    </div>
  );
});

export default InputBox;

