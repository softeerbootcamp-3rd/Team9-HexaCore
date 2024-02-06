import type { ChangeEvent, FocusEventHandler, HTMLInputTypeAttribute } from 'react';

type Props = {
	title?: string,
	placeHolder: string;
	type?: HTMLInputTypeAttribute;
	value: string;
	onBlur?: FocusEventHandler<HTMLInputElement>;
	onChange: (e: ChangeEvent<HTMLInputElement>) => void;
	isWrong?: boolean;
	className?: string;
};

function InputBox({ title, placeHolder, type = "text", value, onChange, onBlur, isWrong = false, className }: Props) {
	return (
		<div className='w-full p-3 flex'>
			<div className='text-background-600 w-24 text-sm pl-5 flex flex-col justify-center'>{title}</div>
			<input className={`
				${isWrong ? 'ring-2 ring-danger-300' : 'focus:ring-2 focus:ring-primary-300'}
				focus:outline-none w-4/5 h-12 text-sm py-2 pl-5 shadow-md rounded-3xl
				${className}`}
				type={type}
				placeholder={placeHolder}
				value={value}
				onChange={onChange}
				onBlur={onBlur}
			/>
		</div >
	);
}

export default InputBox;

