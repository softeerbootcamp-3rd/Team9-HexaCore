import { server } from '@/fetches/common/axios';
import { ResponseWithData } from '@/fetches/common/response.type';
import type { CategoryResponse } from '@/fetches/categories/categories.type';

export const fetchCategories = async (): Promise<CategoryResponse[]> => {
  try {
    const response = await server.get<ResponseWithData<CategoryResponse[]>>('/categories');
    if (!response || !response.success) {
      return [];
    }
    return response.data;
  } catch (e) {
    return [];
  }
};

