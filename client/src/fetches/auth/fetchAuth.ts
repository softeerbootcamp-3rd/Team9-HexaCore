import { server } from "../common/axios";
import { ResponseWithoutData } from "../common/response.type";

export const logout = async () => {
  const response = await server.get<ResponseWithoutData>('/auth/logout');
  
  if (response.success) {
    localStorage.clear();
    window.location.href = '/';
  } else {
    // 로그인 실패한 경우
  }
};