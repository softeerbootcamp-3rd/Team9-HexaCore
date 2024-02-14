import { server } from '@/fetches/common/axios';
import { ResponseWithData } from '@/fetches/common/response.type';
import type { Category, CategoryResult } from '@/fetches/categories/categories.type';

export const fetchCategories = async (): Promise<Category[]> => {
  const response = await server.get<ResponseWithData<CategoryResult>>('/categories');
  if (!response.success) {
    // TODO: response.success가 false인 경우 처리
  }
  return response.data.models;
};

