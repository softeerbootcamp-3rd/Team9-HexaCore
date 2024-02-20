import { redirect, type RouteObject } from 'react-router-dom';
import HostManage from '@/pages/hosts/HostManage';
import HostRegister from '@/pages/hosts/HostRegister';
import { fetchCarDetail, parseCarDetail } from '@/fetches/cars/fetchCarDetail';
import { fetchUser } from '@/fetches/users/fetchUser';

const hostsRoutes: RouteObject[] = [
  {
    path: 'hosts/manage',
    loader: async () => {
      const [carDetailResult] = await Promise.allSettled([fetchCarDetail()]);
      var data = null;
      if (carDetailResult.status == 'fulfilled' && carDetailResult != undefined) {
        if (carDetailResult.value.code === 200) {
          data = parseCarDetail(carDetailResult.value.data);
        } else if (carDetailResult.value.code === 404) {
          return redirect('/hosts/register');
        } else {
          throw Error('예기치 못한 오류가 발생했습니다.');
        }
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

