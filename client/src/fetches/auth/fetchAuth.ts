import { server } from "../common/axios";
import { ResponseWithoutData } from "../common/response.type";

export const logout = async () => {
  const response = await server.get<ResponseWithoutData>('/auth/logout');
  
  if (response.success) {
    localStorage.clear();
    window.location.href = '/';
  } else {
    // 로그아웃 실패한 경우 - 추후 모달을 만들어 모달을 띄우도록 수정
    alert("로그아웃 실패");
  }
};

export const deleteUser = async () => {
  const response = await server.delete<ResponseWithoutData>('/auth');
  
  if (response.success) {
    localStorage.clear();
    window.location.href = '/';
  } else {
    // 회원탈퇴 실패한 경우
    alert(response.message);
  }
};