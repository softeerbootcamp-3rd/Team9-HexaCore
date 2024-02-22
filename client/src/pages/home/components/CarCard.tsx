import type { CarData } from '@/fetches/cars/cars.type';
import { Link } from 'react-router-dom';

type CarCardProps = {
  car: CarData;
  startDate: string;
  endDate: string;
};

function CarCard({ car, startDate, endDate }: CarCardProps) {
  return (
    <div className='mb-4 w-1/3 min-w-[310px] px-2'>
      <Link to={`/cars/${car.id}?startDate=${startDate}&endDate=${endDate}`}>
        <div className='relative flex aspect-square items-center overflow-hidden rounded-xl bg-white'>
          <img src={car.imageUrl} className='absolute h-full w-full object-cover' />
        </div>
        <div className='flex flex-col p-4'>
          <div className='flex justify-between text-lg'>
            <b>{car.subcategory}</b>

            <div className='rounded-xl bg-primary-100 p-2 text-base text-background-500'>{car.capacity}인승</div>
          </div>
          <div className='text-background-500'>
            <div>{car.address}</div>
            <div>연비: {car.mileage}km/L</div>
          </div>
          <div className='ml-auto text-background-500'>
            <b>{car.feePerHour.toLocaleString()}원 / 시간</b>
          </div>
        </div>
      </Link>
    </div>
  );
}

export default CarCard;

