import { stringTuplesToDateRanges } from '@/utils/converters';
import { server } from '../common/axios';
import { ResponseWithData } from '../common/response.type';
import { CarDetailData, CarDetailJsonData } from './cars.type';

export const fetchCarDetail = async () => {
  const response = await server.get<ResponseWithData<CarDetailJsonData>>('/users/cars', {
  });
  return response;  
};

export const parseCarDetail = (carData: CarDetailJsonData): CarDetailData => {
  return {
    id: carData.id,
    categoryName: carData.carName,
    carNumber: carData.carNumber,
    imageUrls: carData.imageUrls,
    mileage: carData.mileage,
    fuel: carData.fuel,
    type: carData.type,
    capacity: carData.capacity,
    year: carData.year,
    feePerHour: carData.feePerHour,
    address: carData.address,
    description: carData.description,
    carDateRanges: stringTuplesToDateRanges(carData.carDateRanges),
    host: carData.host,
    averageRate: carData.averageRate
  };
};
