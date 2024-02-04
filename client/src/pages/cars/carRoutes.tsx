import type { RouteObject } from 'react-router-dom';
import CarDetail from '@/pages/cars/CarDetail';

const carRoutes: RouteObject[] = [
  {
    path: 'cars/:carId',
    loader: async ({ params }) => {
      return params.carId;
    },
    element: <CarDetail />,
  },
];

export default carRoutes;

