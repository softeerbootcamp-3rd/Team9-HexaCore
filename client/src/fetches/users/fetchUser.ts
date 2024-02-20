import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';
import { UserPaymentInfoResponse } from './users.type';

type UserResponse = {
  name: string;
  phoneNumber: string;
  email: string;
  profileImgUrl: string;
};

export type UserData = {
  name: string;
  nickName: string;
  phoneNum: string;
  email: string;
  image: string;
};

export const fetchUser = async (userId: number) => {
  const path = !isNaN(userId) ? `/users/${userId}` : '/users';
  const response = await server.get<ResponseWithData<UserResponse>>(path, {});
  return response;
};

export const parseUser = (userResponseRaw: UserResponse): UserData => {
  const userResponse = userResponseRaw;
  return {
    name: userResponse.name,
    email: userResponse.email,
    phoneNum: userResponse.phoneNumber,
    image: userResponse.profileImgUrl,
  } as UserData;
};

export const fetchUserPaymentInfo = async (): Promise<UserPaymentInfoResponse> => {
  const response = await server.get<ResponseWithData<UserPaymentInfoResponse>>('/users/payment');
  if (!response || !response.success) {
    return {
      customerKey: '',
      name: '',
      hasBillingKey: false,
    };
  }
  return response.data;
};

export const patchUserBillingKey = async (customerKey: string | null, authKey: string | null) => {
  const response = await server.patch<ResponseWithData>('/users/payment', {
    data: {
      customerKey: customerKey,
      authKey: authKey,
    },
  });
  if (!response || !response.success) {
    return false;
  }
  return true;
};

