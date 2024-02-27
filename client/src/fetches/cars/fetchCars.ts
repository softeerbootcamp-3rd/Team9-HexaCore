import { server } from '@/fetches/common/axios';
import { ResponseWithData, ResponseWithPagination } from '@/fetches/common/response.type';
import type { CarData, CarSearchParam, PresignedUrl, PresignedUrlRequestParam } from '@/fetches/cars/cars.type';

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
      categoryId: category,
      subcategoryId: subcategory,
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

export const fetchPresignedUrls = async ({ fileName, fileType, prefix }: PresignedUrlRequestParam) => {
  const response = await server.get<ResponseWithData<PresignedUrl>>(`/cars/presigned-urls?fileName=${fileName}&fileType=${fileType}&prefix=${prefix}`);

  if (!response || !response.success) {
    return null;
  }
  return response.data;
};

