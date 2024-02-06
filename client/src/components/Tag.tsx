type Props = {
	text: string;
	className?: string;
};

function Tag({ text, className }: Props) {
	return (
		<div className={
			'bg-primary-100	text-sm w-16 h-8 px-2 py-2 mr-4 rounded-md flex justify-center items-center'
		}
		>
			{text}
		</div>

	);
}

export default Tag;

