import { redirect, type RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchUser } from '@/fetches/users/fetchUser';
import { CarDetailJsonData } from '@/fetches/cars/cars.type';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';

export type HostManageLoaderData = {
  carDetail: ReturnType<typeof parseCarDetail> | undefined;
  reservations: ReturnType<typeof parseHostReservations>;
};

export type HostRegisterLoaderData = {
  username: string;
  isUpdate: boolean;
  carDetail: CarDetailJsonData | undefined;
};

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const [carDetailResult, HostReservationResult] = await Promise.allSettled([fetchCarDetail(), fetchHostReservations()]);
      const data: HostManageLoaderData = {
        carDetail: undefined,
        reservations: [],
      };
      if (carDetailResult.status == 'fulfilled' && carDetailResult != undefined) {
        if (carDetailResult.value.code === 200) {
          data.carDetail = parseCarDetail(carDetailResult.value.data);
        } else if (carDetailResult.value.code === 404) {
          return redirect('/hosts/register');
        } else {
          throw Error('예기치 못한 오류가 발생했습니다.');
        }
      }

      if (HostReservationResult.status === 'fulfilled' && HostReservationResult.value !== undefined) {
        data.reservations = parseHostReservations(HostReservationResult.value.data);
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
      const [userResponse, carResponse] = await Promise.allSettled([fetchUser(NaN), fetchCarDetail()]);

      if (userResponse.status !== 'fulfilled' || carResponse.status !== 'fulfilled') return null;
      if (userResponse.status === 'fulfilled') {
        if (userResponse.value === undefined) {
          return null;
        }
      }

      const isUpdate: boolean = carResponse.value.success;

      const data: HostRegisterLoaderData = {
        username: userResponse.value.data.name,
        isUpdate: isUpdate,
        carDetail: carResponse.value.data,
      };
      return data;
    },
  },
];

export default hostsRoutes;

