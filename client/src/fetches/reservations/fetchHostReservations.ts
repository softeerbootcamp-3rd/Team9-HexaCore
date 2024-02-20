import { stringTupleToDateTimeRange } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithPagination } from '@/fetches/common/response.type';
import type { ReservationData, ReservationStatus } from "@/fetches/reservations/Reservation.type"

type HostReservationResponse = {
  id: number;
  guest: { id: number; name: string; imageUrl: string; phoneNumber: string};
  rentDateTime: string;
  returnDateTime: string;
  fee: number;
  extraFee: number,
  status: ReservationStatus;
  address: string;
};

export const fetchHostReservations = async (page: number, size: number) => {
  const response = await server.get<ResponseWithPagination<HostReservationResponse[]>>('/reservations/host?page='+page+'&size='+size, {
  });
  if (response.success) {
    return response;
  }
};

export const parseHostReservations = (hostReservationsResponseRaw: HostReservationResponse[]): ReservationData[] => {
  return hostReservationsResponseRaw.map(
    (reservation) =>
      ({
        id: reservation.id,
        target: {
          id: reservation.guest.id,
          name: reservation.guest.name ,
          image: reservation.guest.imageUrl,
          phoneNumber: reservation.guest.phoneNumber,
          lat:null,
          lng:null
        },
        rentPeriod: stringTupleToDateTimeRange([reservation.rentDateTime, reservation.returnDateTime]),
        rentFee: reservation.fee,
        extraFee: reservation.extraFee,
        rentStatus: toReservationStatus(reservation.status),
      }) as ReservationData,
  );
};

const toReservationStatus = (status: ReservationStatus) => {
  return status;
};