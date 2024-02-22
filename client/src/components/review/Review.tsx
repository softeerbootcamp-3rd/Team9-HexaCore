import StarIcon from "./StarIcon";

type Props = {
	key: number;
	imgUrl: string;
	name: string;
	rate: number;
	contents: string;
}

function Review({ key, imgUrl='//defaultProfile.png', name, rate=0, contents }: Props) {
	return (
		<div>
			<div key={key} className='flex flex-col gap-1 w-3/4'>
				<div className='flex items-center gap-1'>
					<div className='flex h-10 w-10 items-center justify-center overflow-hidden rounded-full stroke-2 stroke-background-500'>
						<img
							src={imgUrl} alt='profile' />
					</div>
					<div className='font-medium text-sm'>{name}</div>
				</div>
				<div className='flex gap-1 ml-2 items-center '>
					<StarIcon filled={true} className='w-4 h-4' />
					<div className='text-xs'>{rate}</div>
				</div>
				<p className='ml-2 text-background-500 text-sm '>{contents}</p>
			</div>
		</div>
	);
}

export default Review;
