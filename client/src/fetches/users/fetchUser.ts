import UserDummy from './UserData.dummy.json';

type UserResponseRaw = typeof UserDummy; // TODO: 응답 형식 타입 정의

export type UserData = {
  name: string;
  nickName: string;
  phoneNum: string;
  email: string ;
  image: string;
};

export const fetchUser = async (userId: number) => {
  return UserDummy;
};

export const parseUser = (userResponseRaw: UserResponseRaw): UserData => {
	const userResponse = userResponseRaw.data;
  return {
		name: userResponse.name,
		nickName: userResponse.nickname,
		email: userResponse.email,
		phoneNum: userResponse.phoneNumber,
		image: userResponse.profileImg,
	};
};