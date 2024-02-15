import { DateRange } from '@/components/calendar/calendar.core';
import { stringTupleToDateRange } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';

type HostReservationsResponse = {
  reservations: HostReservationResponse[];
};

type HostReservationResponse = {
  id: number;
  guest: { id: number; name: string; image: string; phoneNumber: string};
  rentDateTime: string;
  returnDateTime: string;
  fee: number;
  rentStatus: ReservationStatus;
  address: string;
  status: string;
};

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

export const fetchHostReservations = async () => {
  const response = await server.get<ResponseWithData<HostReservationsResponse>>('/reservations/host', {
  });
  if (response.success) {
    return response;
  }
};

export const parseHostReservations = (hostReservationsResponseRaw: HostReservationsResponse): ReservationData[] => {
  return hostReservationsResponseRaw.reservations.map(
    (reservation) =>
      ({
        id: reservation.id,
        target: {
          id: reservation.guest.id,
          name: reservation.guest.name ,
          image: reservation.guest.image,
          phoneNumber: reservation.guest.phoneNumber,
        },
        rentPeriod: stringTupleToDateRange([reservation.rentDateTime, reservation.returnDateTime]),
        rentFee: reservation.fee,
        rentStatus: toReservationStatus(reservation.status),
      }) as ReservationData,
  );
};

const toReservationStatus = (status: string): ReservationStatus => {
  const validStatuses: ReservationStatus[] = ['CANCEL', 'READY', 'USING', 'TERMINATED'];
  if (validStatuses.includes(status as ReservationStatus)) {      
    return status as ReservationStatus;
  }
  return "disabled" as ReservationStatus;
};

