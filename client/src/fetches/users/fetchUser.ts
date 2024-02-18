import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';
import { UserCustomerKeyResponse } from './users.type';

type UserResponse = {
  name: string;
  phoneNumber: string;
  email: string ;
  profileImgUrl: string;
};

export type UserData = {
  name: string;
  nickName: string;
  phoneNum: string;
  email: string ;
  image: string;
};

export const fetchUser = async (userId: number) => {
	const path = !isNaN(userId) ? `/users/${userId}` : '/users';
  const response = await server.get<ResponseWithData<UserResponse>>(path, {
  });
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

export const fetchUserCustomerKey = async (): Promise<string> => {
  const response = await server.get<ResponseWithData<UserCustomerKeyResponse>>('/users/customerKey');
  if (!response || !response.success) {
    return '';
  }
  return response.data.customerKey;
}
