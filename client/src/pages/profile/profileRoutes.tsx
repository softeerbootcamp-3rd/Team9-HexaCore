import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import { fetchUser, parseUser } from '@/fetches/users/fetchUser';
import { fetchGuestReservations, parseGuestReservations } from '@/fetches/reservations/fetchGuestReservations';

interface LoaderParams {
  params: {
    userId?: string;
  };
}

type ProfileLoaderData = {
  user: ReturnType<typeof parseUser> | null;
  reservations: ReturnType<typeof parseGuestReservations>;
};

const profileRoutes: RouteObject[] = [
  {
    path: 'profile/:userId?',
    loader: async ({ params }: LoaderParams) => {
      const userId = params.userId ?? localStorage.getItem("userId") ?? '';
      const [userResult, GuestReservationResult] = await Promise.allSettled([fetchUser(parseInt(userId)), fetchGuestReservations()]);
      const data: ProfileLoaderData = {
        user: null,
        reservations: []
      }
      if (userResult.status == 'fulfilled' && userResult.value != undefined){
        if(userResult.value.code === 200){
          data.user = parseUser(userResult.value.data);
        } else if(userResult.value.code === 403){
          location.href = "/auth/login";
        }
        else{
          throw Error("예기치 못한 오류가 발생했습니다.")
        }
      }
      if (GuestReservationResult.status === 'fulfilled' && GuestReservationResult.value !== undefined) {
        data.reservations = parseGuestReservations(GuestReservationResult.value.data);
      }
      return data;
    },
    element: <Profile />,
  },
];

export default profileRoutes;
