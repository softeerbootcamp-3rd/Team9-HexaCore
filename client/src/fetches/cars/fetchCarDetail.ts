import { DateRange } from '@/components/calendar/calendar.core';
import carDetailDummy from './CarDetail.dummy.json';
import { stringTuplesToDateRanges } from '@/utils/converters';

type CarDetailResponseRaw = typeof carDetailDummy; // TODO: 응답 형식 타입 정의

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
  dates: DateRange[];
};

export const fetchCarDetail = async (carId: number) => {
  return carDetailDummy; // TODO: api 호출
};

export const parseCarDetail = (carDataResponseRaw: CarDetailResponseRaw): CarData => {
  const carData = carDataResponseRaw.data.car;
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
    dates: stringTuplesToDateRanges(carData.dates),
  };
};

