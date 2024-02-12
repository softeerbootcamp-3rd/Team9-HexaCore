import { DateRange } from '@/components/calendar/calendar.core';
import HostReservationsDummy from './HostReservationData.dummy.json';
import { stringTupleToDateRange } from '@/utils/converters';

type HostReservationsResponseRaw = typeof HostReservationsDummy; // TODO: 응답 형식 타입 정의

export type ReservationData = {
  id: number;
  target: { id: number; name: string; image: string; phoneNumber: string};
  rentPeriod: DateRange;
  rentFee: number;
  rentStatus: string;
  address: string;
};

export const fetchHostReservations = async () => {
  return HostReservationsDummy;
};

export const parseHostReservations = (hostReservationsResponseRaw: HostReservationsResponseRaw): ReservationData[] => {
  return hostReservationsResponseRaw.data.reservations.map(
    (reservation) =>
      ({
        id: reservation.id,
        target: {
          id: reservation.guest.id,
          name: reservation.guest.nickname ,
          image: reservation.guest.image,
          phoneNumber: reservation.guest.phoneNumber,
        },
        rentPeriod: stringTupleToDateRange([reservation.rentDate, reservation.returnDate]),
        rentFee: reservation.fee,
        rentStatus: reservation.status,
      }) as ReservationData,
  );
};

