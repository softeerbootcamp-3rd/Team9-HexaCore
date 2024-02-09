import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import response from './dummy.json';

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
        const data: UserInfo = {
          name: response.data.name || null,
          nickName: response.data.nickname || null,
          email: response.data.email || null,
          phoneNum: response.data.phoneNumber || null,
          image: response.data.profileImg || null,
        };
        return data;
      } else {
        return null;
      }
    },
    element: <Profile />,
  },
];

export default profileRoutes;

