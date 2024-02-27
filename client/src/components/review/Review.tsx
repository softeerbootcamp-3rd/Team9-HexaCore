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
			<div key={key} className='flex flex-col gap-1 p-2'>
				<div className='flex items-center gap-1'>

					<div className='flex h-9 w-9 items-center justify-center overflow-hidden rounded-full stroke-1 stroke-background-500'>
						<img src={imgUrl} alt='profile' />
					</div>
					<div className='font-medium text-sm ml-1'>{name}</div>
					<StarIcon filled={true} className='w-4 h-4 ml-2' />
					<div className='text-xs'>{rate}</div>

				</div>
				<p className='m-3 text-background-500 text-sm '>{contents}</p>
			</div>
		</div>
	);
}

export default Review;
