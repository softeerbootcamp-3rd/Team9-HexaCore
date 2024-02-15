import { stringTuplesToDateRanges } from '@/utils/converters';
import { server } from '../common/axios';
import { ResponseWithData } from '../common/response.type';
import { CarDetailData, CarDetailJsonData } from './cars.type';

export const fetchCarDetail = async (carId: number) => {
  // 차량 세부 정보 조회 api 호출
  const response = await server.get<ResponseWithData<CarDetailJsonData>>(`/cars/${carId}`);
  console.log(response);

  return response;
};

export const parseCarDetail = (carDetailJsonData: CarDetailJsonData): CarDetailData => {
  const carData = carDetailJsonData;
  console.log(carData);
  return {
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
    host: carData.host
  };
};

