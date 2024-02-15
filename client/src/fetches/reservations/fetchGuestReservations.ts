import { stringTupleToDateRange } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';
import type { ReservationData, ReservationStatus } from "@/fetches/reservations/Reservation.type"

type GuestReservationsResponse = {
  reservations: GuestReservationResponse[];
};

type GuestReservationResponse = {
  id: number;
  car: { id: number; name: string; imageUrl: string;};
  rentDateTime: string;
  returnDateTime: string;
  fee: number;
  status: ReservationStatus;
  carAddress: string;
  hostPhoneNumber: string;
};

export const fetchGuestReservations = async () => {
  const response = await server.get<ResponseWithData<GuestReservationsResponse>>('/reservations/guest', {
  });
  if (response.success) {
    return response;
  }
};

export const parseGuestReservations = (hostReservationsResponseRaw: GuestReservationsResponse): ReservationData[] => {
  return hostReservationsResponseRaw.reservations.map(
    (reservation) =>
      ({
        id: reservation.id,
        target: {
          id: reservation.car.id,
          name: reservation.car.name ,
          image: reservation.car.imageUrl,
          phoneNumber: reservation.hostPhoneNumber
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

