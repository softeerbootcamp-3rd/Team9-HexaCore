import type { RouteObject } from 'react-router-dom';
import response from './CarDetailData.json';
import CarDetail from '@/pages/cars/CarDetail';

export type HostData = {
  name: string,
  profileImg: string
};

export type CarData = {
  carId?: string,
  carName: string,
  carNumber: string,
  imageUrls: string[],
  mileage: number,
  fuel: string,
  type: string,
  capacity: number,
  year: number,
  feePerHour: number,
  carAddress: string,
  description: string,
  dates: string[][],
  host: HostData,
};

const carRoutes: RouteObject[] = [
  {
    path: 'cars/:carId',
    loader: async ({ params }) => {
      const data: CarData = {
        carId: params.carId,
        carName: response.data.car.carName,
        carNumber: response.data.car.carNumber,
        imageUrls: response.data.car.imageUrls,
        mileage: response.data.car.mileage,
        fuel: response.data.car.fuel,
        type: response.data.car.type,
        capacity: response.data.car.capacity,
        year: response.data.car.year,
        feePerHour: response.data.car.feePerHour,
        carAddress: response.data.car.carAddress,
        description: response.data.car.description,
        dates: response.data.car.dates,
        host: response.data.car.host
      }
      return data;
    },
    element: <CarDetail />,
  },
];

export default carRoutes;

