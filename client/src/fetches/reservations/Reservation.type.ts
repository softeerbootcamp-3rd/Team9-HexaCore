import { DateRange } from '@/components/calendar/calendar.core';

export const reservationStatus = {
  CANCEL:'cancel',
  READY:'ready',
  USING: 'using',
  TERMINATED: 'terminated',
  UNDEFINED: null
} as const
export type ReservationStatus = keyof typeof reservationStatus

export type ReservationData = {
  id: number;
  target: { 
    id: number;
    name: string;
    image: string;
    phoneNumber: string,
    address: string | null,
    lat: number | null;
    lng: number | null;
    averageRate: number;
  };
  rentPeriod: DateRange;
  rentFee: number;
  extraFee: number;
  rentStatus: ReservationStatus;
  address: string;
  isReviewed: boolean;
};

export type CreateReservationData = {
  carId: number;
  rentDateTime: string;
  returnDateTime: string;
};