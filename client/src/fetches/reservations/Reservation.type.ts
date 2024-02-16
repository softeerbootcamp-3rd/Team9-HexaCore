import { DateRange } from '@/components/calendar/calendar.core';

export const reservationStatus = {
  CANCEL:'CANCEL',
  READY:'READY',
  USING: 'USING',
  TERMINATED: 'TERMINATED'
} as const
export type ReservationStatus = keyof typeof reservationStatus

export type ReservationData = {
  id: number;
  target: { id: number; name: string; image: string; phoneNumber: string};
  rentPeriod: DateRange;
  rentFee: number;
  rentStatus: ReservationStatus;
  address: string;
  status: ReservationData;
};