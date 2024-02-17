import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import { fetchUser, parseUser } from '@/fetches/users/fetchUser';
import { fetchGuestReservations, parseGuestReservations } from '@/fetches/reservations/fetchGuestReservations';

interface LoaderParams {
  params: {
    userId?: string;
  };
}

const profileRoutes: RouteObject[] = [
  {
    path: 'profile/:userId?',
    loader: async ({ params }: LoaderParams) => {
      const userId = params.userId ?? localStorage.getItem("userId") ?? '';
      const [userResult, GuestReservationResult] = await Promise.allSettled([fetchUser(parseInt(userId)), fetchGuestReservations()]);

      if (userResult.status === 'rejected' || userResult.value === undefined) {
        throw new Error('사용자 정보를 불러오는데 실패했습니다');
      } else if (GuestReservationResult.status === 'rejected' || GuestReservationResult.value === undefined) {
        throw new Error('예약 정보를 불러오는데 실패했습니다');
      } else {
        const user = parseUser(userResult.value.data);
        const reservations = parseGuestReservations(GuestReservationResult.value.data);

        return {
          user: user,
          reservations: reservations,
        };
      }
    },
    element: <Profile />,
  },
];

export default profileRoutes;
