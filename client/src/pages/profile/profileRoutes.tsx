import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import { Reservation } from '@/types/Reservations';
import userResponse from './dummy.json';
import userReserationResponse from './UserReservationData.json';

export type UserInfo = {
  name: string | null;
  nickName: string | null;
  phoneNum: string | null;
  email: string | null;
  image: string | null;
};

const profileRoutes: RouteObject[] = [
  {
    path: 'profile/:userId?',
    loader: async ({ params }) => {
      const userId = params.userId ?? null;
      if (userId !== null) {
        const userData: UserInfo = {
          name: userResponse.data.name || null,
          nickName: userResponse.data.nickname || null,
          email: userResponse.data.email || null,
          phoneNum: userResponse.data.phoneNumber || null,
          image: userResponse.data.profileImg || null,
        };
        const reservationData: Reservation[] = userReserationResponse.data.reservations.map((reservation: any) => {
          return {
            id: reservation.id || null,
            target: {
              id: reservation.car.id || null,
              name: reservation.car.name || null,
              image: reservation.car.imageUrl || null,
              phoneNumber: reservation.hostPhoneNumber || null,
            },
            fee: reservation.fee || null,
            address: reservation.carAddress || null,
            startDate: new Date(reservation.rentDate) || null,
            endDate: new Date(reservation.returnDate) || null,
            status: reservation.status || null,
          };
        });
        return {
          user: userData,
          reservations: reservationData,
        };
      } else {
        return null;
      }
    },
    element: <Profile />,
  },
];

export default profileRoutes;
