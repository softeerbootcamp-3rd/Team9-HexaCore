import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import response from './dummy.json';

type UserInfo = {
  name: String | null;
  nickName: String | null;
  phoneNum: String | null;
  email: String | null;
  image: String | null;
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
