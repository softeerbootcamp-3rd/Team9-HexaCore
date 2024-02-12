import type { RouteObject } from 'react-router-dom';
import Home from '@/pages/home/Home';
import response from './dummy/category.json';

export type Category = {
  category: string;
  subCategory: string;
};

const homeRoutes: RouteObject[] = [
  {
    index: true,
    loader: async () => {
      const data: Category[] = response.data.models;
      return data;
    },
    element: <Home />,
  },
];

export default homeRoutes;
