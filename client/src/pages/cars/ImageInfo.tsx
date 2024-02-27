type Props = {
	img: string;
	alt?: string;
	title: string;
	info: string;
	className?: string;
}

function ImageInfo({ img, alt, title, info, className }: Props) {
	return (
		<div className='flex items-center gap-6 mb-2'>
			<div className={
				`flex h-11 w-11 items-center justify-center
				overflow-hidden rounded-full 
				${className}`}>
				<img src={img ?? '/default-profile.svg'} alt={alt} />
			</div>
			<div className='flex flex-col'>
				<p className='font-semibold text-background-700 text-[17px]'>{title}</p>
				<p className='text-background-500 text-[14px] mt-1'>{info}</p>
			</div>
		</div>
	);
}

export default ImageInfo;