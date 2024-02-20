import { redirect, type RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';
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
      const data: HostManageLoaderData = {
        carDetail: null,
        hostReservations: [],
      };
      if (carDetailResult.status == 'fulfilled' && carDetailResult.value != undefined) {
        if (carDetailResult.value.code === 200) {
          data.carDetail = parseCarDetail(carDetailResult.value.data);

        } else if (carDetailResult.value.code === 404) {
          return redirect('/hosts/register');
        } else {
          throw Error('예기치 못한 오류가 발생했습니다.');
        }
      }
      if (HostReservationResult.status === 'fulfilled' && HostReservationResult.value !== undefined) {
        data.hostReservations = parseHostReservations(HostReservationResult.value.data);
      }
      return data;
    },
    element: <HostManage />,
  },
  {
    path: 'hosts/register',
    element: <HostRegister />,
    loader: async () => {
      // fetchUser(NaN)이면 로그인한 사용자의 정보를 받아온다.
      const response = await fetchUser(NaN);
      if (response === undefined) throw Error('예기치 못한 오류가 발생했습니다.');

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

