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
  target: { id: number; name: string; image: string; phoneNumber: string};
  rentPeriod: DateRange;
  rentFee: number;
  rentStatus: ReservationStatus;
  address: string;
};