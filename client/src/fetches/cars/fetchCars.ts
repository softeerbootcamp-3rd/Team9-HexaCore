import { server } from '@/fetches/common/axios';
import { ResponseWithPagination } from '@/fetches/common/response.type';
import type { CarData, CarSearchParam } from '@/fetches/cars/cars.type';

export const PAGE_SORT = 'id,DESC';
export const PAGE_SIZE = 20;
export const DISTANCE = 5000;

export const fetchCars = async ({ lat, lng, startDate, endDate, party, type, category, subcategory, minPrice, maxPrice, page, size, sort }: CarSearchParam) => {
  const response = await server.get<ResponseWithPagination<CarData[]>>('/cars', {
    params: {
      lat: lat,
      lng: lng,
      distance: DISTANCE,
      startDate: startDate,
      endDate: endDate,
      party: party,
      type: type,
      category: category,
      subcategory: subcategory,
      minPrice: minPrice,
      maxPrice: maxPrice,
      page: page ? page : 0,
      size: size ? size : PAGE_SIZE,
      sort: sort ? sort : PAGE_SORT,
    },
  });
  if (!response || !response.success) {
    return null;
  }
  return { data: response.data, pageInfo: response.pageInfo };
};

