import type { MouseEventHandler } from 'react';

const BUTTON_TYPE = ['enabled', 'disabled', 'danger'] as const;

type Props = {
  text: string;
  type?: (typeof BUTTON_TYPE)[number];
  isRounded?: boolean;
  onClick?: MouseEventHandler<HTMLButtonElement>;
  className?: string;
};

function Button({ text, type = 'enabled', isRounded = false, onClick, className }: Props) {
  return (
    <button
      className={`
			text-sm px-4 py-2
      ${isRounded ? 'rounded-full' : 'rounded-lg'}
			${
        type === 'enabled'
          ? 'bg-primary text-white hover:bg-primary-600'
          : type === 'danger'
          ? 'bg-danger text-white hover:bg-danger-500'
          : 'bg-background-300 text-background-700 cursor-not-allowed'
      }
			${className}
		`}
      disabled={type === 'disabled'}
      onClick={onClick}>
      {text}
    </button>
  );
}

export default Button;
