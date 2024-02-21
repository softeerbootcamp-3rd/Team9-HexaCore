import { stringTupleToDateTimeRange } from '@/utils/converters';
import { server } from '@/fetches/common/axios';
import type { ResponseWithPagination } from '@/fetches/common/response.type';
import type { ReservationData, ReservationStatus } from "@/fetches/reservations/Reservation.type"

type GuestReservationResponse = {
  id: number;
  car: { id: number; name: string; imageUrl: string; lat: number; lng: number;};
  rentDateTime: string;
  returnDateTime: string;
  fee: number;
  extraFee: number,
  status: ReservationStatus;
  carAddress: string;
  hostPhoneNumber: string;
  isReviewed: boolean;
};

export const fetchGuestReservations = async (page: number,size: number) => {
  const response = await server.get<ResponseWithPagination<GuestReservationResponse[]>>('/reservations/guest?page='+page+'&size='+size, {
  });
  if (response.success) {
    return response;
  }
};

export const parseGuestReservations = (guestReservationsResponseRaw: GuestReservationResponse[]): ReservationData[] => {
  return guestReservationsResponseRaw.map(
    (reservation) =>
      ({
        id: reservation.id,
        target: {
          id: reservation.car.id,
          name: reservation.car.name,
          image: reservation.car.imageUrl,
          phoneNumber: reservation.hostPhoneNumber,
          address: reservation.carAddress,
          lat: reservation.car.lat,
          lng: reservation.car.lng
        },
        rentPeriod: stringTupleToDateTimeRange([reservation.rentDateTime, reservation.returnDateTime]),
        rentFee: reservation.fee,
        extraFee: reservation.extraFee,
        rentStatus: toReservationStatus(reservation.status),
        isReviewed: reservation.isReviewed
      }) as ReservationData,
  );
};

const toReservationStatus = (status: ReservationStatus) => {
  return status;
};

