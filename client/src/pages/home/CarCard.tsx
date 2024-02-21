import StarIcon from '@/components/StarIcon';
import Tag from '@/components/Tag';
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
        <div className='flex flex-col p-4 gap-2'>
          <div className='flex justify-between'>
            <div className='flex gap-2 items-center'>
              <b>{car.subcategory}</b>
              {car.averageRate &&
              <div className='flex gap-1 items-center'>
                <StarIcon filled={true} className='w-4 h-4' />
                <div className='text-sm'>{car.averageRate ?? 0}</div>
              </div> }
            </div>
            <Tag text={`${car.capacity}인승`} className='h-6 mr-0' />
          </div>
          <div className='text-background-500'>
            <div className='text-sm'>{car.address}</div>
            <div className='text-sm'>연비: {car.mileage}km/L</div>
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

