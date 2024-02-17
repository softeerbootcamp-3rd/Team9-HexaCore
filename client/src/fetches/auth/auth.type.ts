export type LoginResponse = { 
  tokens: {
    accessToken: string; 
    refreshToken: string ;
  };
  loginUserInfo: {
    userId: number;
    name: string;
    email: string;
    phoneNumber: string;
    profileImgUrl: string
  }
};