type Props = {
	img: string;
	alt?: string;
	title: string;
	info: string;
	className?: string;
}

function ImageInfo({ img, alt, title, info, className }: Props) {
	return (
		<div className='flex items-center gap-4'>
			<div className={
				`flex h-10 w-10 items-center justify-center
				overflow-hidden rounded-full 
				${className}`}>
				<img src={img ?? '/default-profile.svg'} alt={alt} />
			</div>
			<div className='flex flex-col'>
				<p className='font-semibold'>{title}</p>
				<p className='text-background-500'>{info}</p>
			</div>
		</div>
	);
}

export default ImageInfo;