import { Params, RouteObject } from 'react-router-dom';
import CarDetail from '@/pages/cars/CarDetail';
import { parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { CarDetailData, CarDetailJsonData } from '@/fetches/cars/cars.type';
import { ResponseWithData } from '@/fetches/common/response.type';
import { server } from '@/fetches/common/axios';

export type CarData = {
  carId: number;
} & CarDetailData;

const carRoutes: RouteObject[] = [
  {
    path: 'cars/:carId',
    loader: async ({ params } : { params : Params}) => {
      const carId = parseInt(params.carId ?? '');
      const response = await server.get<ResponseWithData<CarDetailJsonData>>(`/cars/${carId}`);
      console.log(response);
      
      if(!response.success) {
        return null;
      }

      const responseData = parseCarDetail(response.data);
      const data : CarData = {
        carId,
        ...responseData
      };

      return data;
    },
    element: <CarDetail />,
  },
];

export default carRoutes;

