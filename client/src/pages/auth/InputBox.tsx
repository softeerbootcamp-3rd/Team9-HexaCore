import { forwardRef, type FocusEventHandler, type HTMLInputTypeAttribute } from 'react';

type Props = {
	title?: string,
	placeHolder: string;
	type?: HTMLInputTypeAttribute;
	onBlur?: FocusEventHandler<HTMLInputElement>;
	isWrong?: boolean;
	className?: string;
};

const InputBox = forwardRef<HTMLInputElement, Props>(({ title, placeHolder, type = "text", onBlur, isWrong = false, className }, ref) => {
	return (
		<div className='w-full p-3 flex'>
			<div className='text-background-600 w-24 text-sm pl-5 flex flex-col justify-center'>{title}</div>
			<input className={`
				${isWrong ? 'ring-2 ring-danger-300' : 'focus:ring-2 focus:ring-primary-300'}
				focus:outline-none w-4/5 h-12 text-sm py-2 pl-5 shadow-md rounded-3xl
				${className}`}
				type={type}
				placeholder={placeHolder}
				onBlur={onBlur}
				ref={ref}
			/>
		</div >
	);
})

export default InputBox;

