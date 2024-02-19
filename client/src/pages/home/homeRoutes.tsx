import type { RouteObject } from 'react-router-dom';
import Home from '@/pages/home/Home';
import type { CategoryResponse } from '@/fetches/categories/categories.type';
import type { CarData, CarSearchParam } from '@/fetches/cars/cars.type';
import { fetchCategories } from '@/fetches/categories/fetchCategories';
import { fetchCars } from '@/fetches/cars/fetchCars';

export type HomeLoaderResponse = {
  categories: CategoryResponse[];
  cars: {
    data: CarData[];
    pageInfo: {
      page: number;
      size: number;
      hasNext: boolean;
    };
  } | null;
};

const homeRoutes: RouteObject[] = [
  {
    index: true,
    loader: async ({ request }): Promise<HomeLoaderResponse> => {
      const categories: CategoryResponse[] = await fetchCategories();
      const carSearchParam = parseQueryString(request.url);
      const cars = carSearchParam ? await fetchCars(carSearchParam) : null;
      return { categories: categories, cars: cars };
    },
    element: <Home />,
  },
];

export const parseQueryString = (url: string) => {
  const params = new URLSearchParams(new URL(url).search);
  const lat = params.get('lat');
  const lng = params.get('lng');
  const startDate = params.get('startDate');
  const endDate = params.get('endDate');
  const party = params.get('party');
  const type = params.get('type');
  const category = params.get('category');
  const subcategory = params.get('subcategory');
  const minPrice = params.get('minPrice');
  const maxPrice = params.get('maxPrice');

  if (lat && lng && startDate && endDate && party) {
    const a: CarSearchParam = {
      lat: Number(lat),
      lng: Number(lng),
      startDate: startDate,
      endDate: endDate,
      party: party,
      ...(type && { type: type }),
      ...(category && { category: category }),
      ...(subcategory && { subcategory: subcategory }),
      ...(minPrice && { minPrice: Number(minPrice) }),
      ...(maxPrice && { maxPrice: Number(maxPrice) }),
    };
    return a;
  }
};

export default homeRoutes;

