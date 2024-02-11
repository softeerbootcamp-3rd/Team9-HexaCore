import { Link } from 'react-router-dom';

export type CarData = {
  id: number;
  carName: string;
  imageUrl: string;
  carAddress: string;
  mileage: number;
  capacity: number;
  feePerHour: number;
};

function CarCard({ id, imageUrl, feePerHour, capacity, carName, carAddress, mileage }: CarData) {
  return (
    <div className="w-1/3 min-w-[310px] px-2 mb-4">
      <Link to={`/cars/${id}`}>
        <div className="relative flex items-center overflow-hidden rounded-xl aspect-square bg-white">
          <img src={imageUrl} className="absolute w-full h-full object-cover" />
        </div>
        <div className="flex flex-col p-4">
          <div className="flex justify-between text-lg">
            <b>{carName}</b>

            <div className="rounded-xl text-base bg-primary-100 p-2 text-background-500">{capacity}인승</div>
          </div>
          <div className="text-background-500">
            <div>{carAddress}</div>
            <div>연비: {mileage}km/L</div>
          </div>
          <div className="ml-auto text-background-500">
            <b>{feePerHour.toLocaleString()}원 / 시간</b>
          </div>
        </div>
      </Link>
    </div>
  );
}

export default CarCard;

