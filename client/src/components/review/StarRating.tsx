import StarIcon from './StarIcon';

type Props = {
	rate: number;
	onClick: (rate: number) => void;
}

function StarRating({ rate, onClick }: Props) {

	return (
		<div className='flex'>
			{Array.from({ length: 5 }, (_, i) => (
				<StarIcon
				key={i}
				filled={i < rate}
				onClick={() => onClick(i + 1)}
			/>
			))}
		</div>
	);
}

export default StarRating;
