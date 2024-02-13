import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import { fetchUser, parseUser } from '@/fetches/users/fetchUser';
import { fetchHostReservations, parseHostReservations } from '@/fetches/reservations/fetchHostReservations';

const profileRoutes: RouteObject[] = [
  {
    path: 'profile/:userId?',
    loader: async ({ params }) => {
      const userId = params.userId ?? null;
      if(userId === null){
        throw new Error("사용자 정보를 불러오는데 실패했습니다")//Todo : redirect
      }
      const [userResult, HostReservationResult] = await Promise.allSettled([fetchUser(parseInt(userId)), fetchHostReservations()]);
      
      if (userResult.status === 'rejected') {
        throw new Error('사용자 정보를 불러오는데 실패했습니다');
      }
      if (HostReservationResult.status === 'rejected') {
        throw new Error('예약 정보를 불러오는데 실패했습니다');
      }
      
      const user = parseUser(userResult.value)
      const reservations = parseHostReservations(HostReservationResult.value)
        
      return {
        user: user,
        reservations: reservations,
      };
    },
    element: <Profile />,
  },
];

export default profileRoutes;
