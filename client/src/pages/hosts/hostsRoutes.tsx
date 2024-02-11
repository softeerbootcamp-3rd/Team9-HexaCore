import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
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
  dates: string[] | null;
};
export type Reservation = {
  id: number | undefined;
  target: { id: number | undefined; name: string | null; image: string | null; phoneNumber: string | null };
  fee: number | undefined;
  address: string | null;
  startDate: string | null;
  endDate: string | null;
  status: string | null;
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
        dates: carResponse.data.car.dates || null,
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
          address: carData.carAddress || null,
          startDate: reservation.rentDate || null,
          endDate: reservation.returnDate || null,
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
