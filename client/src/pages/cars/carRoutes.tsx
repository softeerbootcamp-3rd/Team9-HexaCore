import { Params, RouteObject } from 'react-router-dom';
import CarDetail from '@/pages/cars/CarDetail';
import { parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { CarDetailData, CarDetailJsonData } from '@/fetches/cars/cars.type';
import { ResponseWithData } from '@/fetches/common/response.type';
import { server } from '@/fetches/common/axios';
import { fetchCarReviews } from '@/fetches/reviews/fetchReviews';
import { CarReviewResponse } from '@/fetches/reviews/Review.type';

type CarData = {
  carId: number;
} & CarDetailData;

export type CarDetailLoaderData = {
  carData: CarData;
  review: CarReviewResponse[];
  hasNext: boolean;
  totalReviews: number;
}

const carRoutes: RouteObject[] = [
  {
    path: 'cars/:carId',
    loader: async ({ params } : { params : Params}) => {
      const carId = parseInt(params.carId ?? '');
      const responseCarDetail = await server.get<ResponseWithData<CarDetailJsonData>>(`/cars/${carId}`);
      const responseReview = await fetchCarReviews(carId, 0, 10, 'id,DESC');
      
      if(!responseCarDetail.success || !responseReview?.success) {
        return null;
      }

      const responseCarDetailData = parseCarDetail(responseCarDetail.data);
      const carData : CarData = {
        carId,
        ...responseCarDetailData
      };

      const data : CarDetailLoaderData = {
        carData: carData,
        review: responseReview.data,
        hasNext: responseReview.pageInfo.hasNext,
        totalReviews: responseReview.pageInfo.totalElements
      }

      return data;
    },
    element: <CarDetail />,
  },
];

export default carRoutes;
