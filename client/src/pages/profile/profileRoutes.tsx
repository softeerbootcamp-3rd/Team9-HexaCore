import type { RouteObject } from 'react-router-dom';
import Profile from '@/pages/profile/Profile';

const profileRoutes: RouteObject[] = [
  {
    path: 'profile/:userId?',
    loader: async ({ params }) => {
      const userId = params.userId ?? 'my-userId';
      // TODO: fetch user data
      return userId;
    },
    element: <Profile />,
  },
];

export default profileRoutes;

