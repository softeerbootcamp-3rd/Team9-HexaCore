import { server } from '@/fetches/common/axios';
import type { ResponseWithData } from '@/fetches/common/response.type';

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
  if (response.success) {
    return response;
  }
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