export type ResponseWithoutData = {
  success: boolean;
  code: number;
  message: string;
};

export type ResponseWithData<T = unknown> = ResponseWithoutData & {
  data: T;
};

export type ResponseWithPagination<T = unknown> = ResponseWithData<T> & {
  pageInfo: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
};

