import { DateRange } from '@/components/calendar/calendar.core';

export type ReservationStatus = 'CANCEL' | 'READY' | 'USING' | 'TERMINATED';

export type ReservationData = {
  id: number;
  target: { id: number; name: string; image: string; phoneNumber: string};
  rentPeriod: DateRange;
  rentFee: number;
  rentStatus: ReservationStatus;
  address: string;
  status: ReservationData;
};