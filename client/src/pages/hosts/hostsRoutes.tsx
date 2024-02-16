import type { RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail'
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';
import { ReservationData } from '@/fetches/reservations/Reservation.type';
import { CarDetailData } from '@/fetches/cars//cars.type';

export type HostManageLoaderData = {
  carDetail: ReturnType<typeof parseCarDetail> | undefined;
  hostReservations: ReturnType<typeof parseHostReservations>;
};

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const [carDetailResult, HostReservationResult] = await Promise.allSettled([fetchCarDetail(), fetchHostReservations()]);
      var data: HostManageLoaderData = {
        carDetail: undefined,
        hostReservations: []
      }
      if (carDetailResult.status == 'fulfilled'){
        if(carDetailResult.value.code === 200){
          data.carDetail = parseCarDetail(carDetailResult.value.data);
        } else if(carDetailResult.value.code === 404){
          location.href = "/hosts/register"
        }
        else{
          throw Error("예기치 못한 오류가 발생했습니다.")
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
  },
];

export default hostsRoutes;
