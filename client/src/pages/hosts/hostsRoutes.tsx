import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { fetchUser } from '@/fetches/users/fetchUser';

export type HostManageLoaderData = {
  carDetail: ReturnType<typeof parseCarDetail> | null;
  hostReservations: ReturnType<typeof parseHostReservations>;
};

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const [carDetailResult, HostReservationResult] = await Promise.allSettled([fetchCarDetail(), fetchHostReservations()]);
      var carDetail = null;
      var hostReservations: ReservationData[] = [];
      if (carDetailResult.status === 'fulfilled' && carDetailResult.value !== undefined) {
        carDetail = parseCarDetail(carDetailResult.value.data);
      } else if (HostReservationResult.status === 'fulfilled' && HostReservationResult.value !== undefined) {
        hostReservations = parseHostReservations(HostReservationResult.value.data);
      }
      const data: HostManageLoaderData = {
        carDetail,
        hostReservations,
      };
      return data;
    },
    element: <HostManage />,
  },
  {
    path: 'hosts/register',
    element: <HostRegister />,
    loader: async () => {
      const response = await fetchUser(NaN);

      if (response === undefined || response.success === false) {
        location.href = '/auth/login';
        return;
      }

      const carResponse = await fetchCarDetail();
      const isUpdate: boolean = carResponse?.success ?? false;

      const data = {
        username: response.data.name,
        isUpdate: isUpdate,
        carDetail: carResponse?.data,
      };
      return data;
    },
  },
];

export default hostsRoutes;

