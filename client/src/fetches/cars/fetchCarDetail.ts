import { DateRange } from '@/components/calendar/calendar.core';
import { stringTuplesToDateRanges } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';

type CarDetailResponse = {
  carName: string;
  carNumber: string;
  imageUrls: string[];
  mileage: number;
  fuel: string;
  type: string;
  capacity: number;
  year: number;
  feePerHour: number;
  carAddress: string;
  description: string;
  carDateRanges: string[][];
};

export type CarData = {
  carName: string;
  carNumber: string;
  imageUrls: string[];
  mileage: number;
  fuel: string;
  type: string;
  capacity: number;
  year: number;
  feePerHour: number;
  carAddress: string;
  description: string;
  carDateRanges: DateRange[];
};

export const fetchCarDetail = async () => {
  const response = await server.get<ResponseWithData<CarDetailResponse>>('/users/cars', {
  });
  if (response.success) {
    return response;
  }
};

export const parseCarDetail = (carData: CarDetailResponse): CarData => {
  return {
    carName: carData.carName,
    carNumber: carData.carNumber,
    imageUrls: carData.imageUrls,
    mileage: carData.mileage,
    fuel: carData.fuel,
    type: carData.type,
    capacity: carData.capacity,
    year: carData.year,
    feePerHour: carData.feePerHour,
    carAddress: carData.carAddress,
    description: carData.description,
    carDateRanges: stringTuplesToDateRanges(carData.carDateRanges),
  };
};

