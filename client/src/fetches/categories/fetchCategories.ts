import { server } from '@/fetches/common/axios';
import { ResponseWithData } from '@/fetches/common/response.type';
import type { CategoryResponse } from '@/fetches/categories/categories.type';

export const fetchCategories = async (): Promise<CategoryResponse[]> => {
  const response = await server.get<ResponseWithData<CategoryResponse[]>>('/categories');
  if (!response || !response.success) {
    // TODO: response.success가 false인 경우 처리
    return [];
  }
  return response.data;
};

