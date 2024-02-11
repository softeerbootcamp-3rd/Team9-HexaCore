import { DateRange } from '@/components/calendar/calendar.core';
import HostReservationsDummy from './HostReservationData.dummy.json';
import { stringTupleToDateRange } from '@/utils/converters';

type HostReservationsResponseRaw = typeof HostReservationsDummy; // TODO: 응답 형식 타입 정의

export type HostReservationData = {
  id: number;
  guestId: number;
  guestNickname: string;
  guestPhoneNumber: string;
  guestImage: string;
  rentPeriod: DateRange;
  rentFee: number;
  rentStatus: string;
};

export const fetchHostReservations = async () => {
  return HostReservationsDummy;
};

export const parseHostReservations = (hostReservationsResponseRaw: HostReservationsResponseRaw): HostReservationData[] => {
  return hostReservationsResponseRaw.data.reservations.map(
    (reservation) =>
      ({
        id: reservation.id,
        guestId: reservation.guest.id,
        guestNickname: reservation.guest.nickname,
        guestPhoneNumber: reservation.guest.phoneNumber,
        guestImage: reservation.guest.image,
        rentPeriod: stringTupleToDateRange([reservation.rentDate, reservation.returnDate]),
        rentFee: reservation.fee,
        rentStatus: reservation.status,
      }) as HostReservationData,
  );
};

