import type { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import type { ResponseWithoutData } from './response.type';
import axios, { isAxiosError } from 'axios';

const baseURL = import.meta.env.MODE === 'production' ? import.meta.env.VITE_BASE_URL : 'http://localhost:8080';
const apiPrefix = import.meta.env.VITE_API_PREFIX ?? '/';

const axiosInstance = axios.create({
  baseURL: `${baseURL}${apiPrefix}`,
});

// 요청 인터셉터 추가
axiosInstance.interceptors.request.use(
  function (config) {
    // 헤더에 Authorization 설정
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  function (error) {
    // 요청이 실패한 경우에 대한 처리
    return Promise.reject(error);
  },
);

axiosInstance.interceptors.response.use(
  (response) => response.data,
  async (error: AxiosError<ResponseWithoutData>) => {
    if (!(isAxiosError(error) && error.response)) return Promise.reject(error);
    const { status, data } = error.response;

    if (status === 403) {
      // 403 유효하지 않은 토큰
      localStorage.clear();
      window.location.href = '/auth/login';
      return;
    }

    if (status === 401) {
      // 401 만료된 토큰
      localStorage.clear();
      window.location.href = '/auth/login'; // TODO: refresh 토큰으로 토큰 재발급
    }

    return data;
  },
);

type HTTPMethod = 'get' | 'post' | 'patch' | 'put' | 'delete';
const attachMethod =
  (method: HTTPMethod) =>
  (axiosInstance: AxiosInstance) =>
  <T = unknown>(url: string, config?: Omit<AxiosRequestConfig, 'url' | 'method'>): Promise<T> =>
    axiosInstance(url, { method, ...config });

export const server = {
  get: attachMethod('get')(axiosInstance),
  post: attachMethod('post')(axiosInstance),
  patch: attachMethod('patch')(axiosInstance),
  put: attachMethod('put')(axiosInstance),
  delete: attachMethod('delete')(axiosInstance),
} as const;

