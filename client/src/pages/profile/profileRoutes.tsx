import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';
import { fetchUser, parseUser } from '@/fetches/users/fetchUser';

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
      const [userResult] = await Promise.allSettled([fetchUser(parseInt(userId))]);

      if (userResult.status == 'fulfilled' && userResult.value != undefined){
        if(userResult.value.code === 200){
          return parseUser(userResult.value.data);
        }
        //TODO:통신실패시 동작
      }

      return null;
    },
    element: <Profile />,
  },
];

export default profileRoutes;
