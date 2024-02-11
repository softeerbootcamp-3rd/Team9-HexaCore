import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';

export type HostManageLoaderData = {
  carDetail: ReturnType<typeof parseCarDetail>;
  hostReservations: ReturnType<typeof parseHostReservations>;
};

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const carId = 1;
      const [carDetailResult, HostReservationResult] = await Promise.allSettled([fetchCarDetail(carId), fetchHostReservations()]);

      if (carDetailResult.status === 'rejected') {
        throw new Error('차량 정보를 불러오는데 실패했습니다'); // TODO: retry and default value
      }
      if (HostReservationResult.status === 'rejected') {
        throw new Error('예약 정보를 불러오는데 실패했습니다');
      }

      const carDetail = parseCarDetail(carDetailResult.value);
      const hostReservations = parseHostReservations(HostReservationResult.value);
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
  },
];

export default hostsRoutes;

