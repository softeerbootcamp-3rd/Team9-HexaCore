import type { RouteObject } from 'react-router-dom';
import Home from '@/pages/home/Home';
import type { Category } from '@/fetches/categories/categories.type';
import { fetchCategories } from '@/fetches/categories/fetchCategories';

const homeRoutes: RouteObject[] = [
  {
    index: true,
    loader: async () => {
      const data: Category[] = await fetchCategories();
      return data;
    },
    element: <Home />,
  },
];

export default homeRoutes;

