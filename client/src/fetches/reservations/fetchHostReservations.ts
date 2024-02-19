import { stringTupleToDateTimeRange } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';
import type { ReservationData, ReservationStatus } from "@/fetches/reservations/Reservation.type"

type HostReservationsResponse = {
  reservations: HostReservationResponse[];
};

type HostReservationResponse = {
  id: number;
  guest: { id: number; name: string; imageUrl: string; phoneNumber: string};
  rentDateTime: string;
  returnDateTime: string;
  fee: number;
  status: ReservationStatus;
  address: string;
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
          image: reservation.guest.imageUrl,
          phoneNumber: reservation.guest.phoneNumber,
          lat:null,
          lng:null
        },
        rentPeriod: stringTupleToDateTimeRange([reservation.rentDateTime, reservation.returnDateTime]),
        rentFee: reservation.fee,
        rentStatus: toReservationStatus(reservation.status),
      }) as ReservationData,
  );
};

const toReservationStatus = (status: ReservationStatus) => {
  return status;
};