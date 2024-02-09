import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import response from './CarData.json';

export type CarData = {
  carName: string;
  carNumber: string;
  imageUrls: string[];
  mileage: Number;
  fuel: string;
  type: string;
  capacity: Number;
  year: Number;
  feePerHour: Number;
  carAddress: string;
  description: string;
  dates: string[];
};
const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const data: CarData = {
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
      };
      return data;
    },
    element: <HostManage />,
  },
  {
    path: 'hosts/register',
    element: <HostRegister />,
  },
];

export default hostsRoutes;
