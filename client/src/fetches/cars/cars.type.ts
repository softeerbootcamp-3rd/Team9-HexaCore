import { DateRange } from '@/components/calendar/calendar.core';

export type HostData = {
	name: string;
	profileImg: string;
}

export type CarDetailData = {
  categoryName: string;
  carNumber: string;
  imageUrls: string[];
  mileage: number;
  fuel: string;
  type: string;
  capacity: number;
  year: number;
  feePerHour: number;
  address: string;
  description: string;
  carDateRanges: DateRange[];
	host: HostData;
};

export type CarDetailJsonData = {
  carName: string;
  carNumber: string;
  imageUrls: string[];
  mileage: number;
  fuel: string;
  type: string;
  capacity: number;
  year: number;
  feePerHour: number;
  address: string;
  description: string;
  carDateRanges: string[][];
	host: HostData;
}