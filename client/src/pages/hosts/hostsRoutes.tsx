import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';
import { Navigate } from 'react-router-dom';

export type HostManageLoaderData = {
  carDetail: ReturnType<typeof parseCarDetail>;
  hostReservations: ReturnType<typeof parseHostReservations>;
};

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const [carDetailResult, HostReservationResult] = await Promise.allSettled([fetchCarDetail(), fetchHostReservations()]);

      if (carDetailResult.status === 'rejected' || carDetailResult.value === undefined) {
        alert('차량 정보를 불러오는데 실패했습니다');
        location.reload();
      } else if (HostReservationResult.status === 'rejected' || HostReservationResult.value === undefined) {
        alert('예약 정보를 불러오는데 실패했습니다');
        location.reload();
      } else {
        const carDetail = parseCarDetail(carDetailResult.value.data);
        const hostReservations = parseHostReservations(HostReservationResult.value.data);
        const data: HostManageLoaderData = {
          carDetail,
          hostReservations,
        };
        return data;
      }
    },
    element: <HostManage />,
  },
  {
    path: 'hosts/register',
    element: <HostRegister />,
  },
];

export default hostsRoutes;
