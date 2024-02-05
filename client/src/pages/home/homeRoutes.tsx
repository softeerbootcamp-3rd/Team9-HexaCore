import type { RouteObject } from 'react-router-dom';
import Home from '@/pages/home/Home';

const homeRoutes: RouteObject[] = [
  {
    index: true,
    element: <Home />,
  },
];

export default homeRoutes;

