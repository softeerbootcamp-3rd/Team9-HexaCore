import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { Reservation } from '@/types/Reservations';
import carResponse from './CarData.json';
import carReserationResponse from './CarReservationData.json';
export type CarData = {
  name: string | null;
  number: string | null;
  imageUrls: string[] | null;
  mileage: number | undefined;
  fuel: string | null;
  type: string | null;
  capacity: number | undefined;
  year: number | undefined;
  feePerHour: number | undefined;
  carAddress: string | null;
  description: string | null;
  dates: Date[] | null;
};
const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const carData: CarData = {
        name: carResponse.data.car.carName || null,
        number: carResponse.data.car.carNumber || null,
        imageUrls: carResponse.data.car.imageUrls || null,
        mileage: carResponse.data.car.mileage || undefined,
        fuel: carResponse.data.car.fuel || null,
        type: carResponse.data.car.type || null,
        capacity: carResponse.data.car.capacity || undefined,
        year: carResponse.data.car.year || undefined,
        feePerHour: carResponse.data.car.feePerHour || undefined,
        carAddress: carResponse.data.car.carAddress || null,
        description: carResponse.data.car.description || null,
        dates: carResponse.data.car.dates.map((dateString) => new Date(dateString)) || null,
      };
      const reservationData: Reservation[] = carReserationResponse.data.reservations.map((reservation: any) => {
        return {
          id: reservation.id || null,
          target: {
            id: reservation.guest.id || null,
            name: reservation.guest.nickname || null,
            image: reservation.guest.image || null,
            phoneNumber: reservation.guest.phoneNumber || null,
          },
          fee: reservation.fee || null,
          address: reservation.carAddress || null,
          startDate: new Date(reservation.rentDate) || null,
          endDate: new Date(reservation.returnDate) || null,
          status: reservation.status || null,
        };
      });
      return {
        car: carData,
        reservations: reservationData,
      };
    },
    element: <HostManage />,
  },
  {
    path: 'hosts/register',
    element: <HostRegister />,
  },
];

export default hostsRoutes;
