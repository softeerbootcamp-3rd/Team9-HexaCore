import Button from '@/components/Button';
import React, { useEffect } from 'react';
import { UserInfo } from '@/profile/profileRoutes';
import { useNavigate } from 'react-router-dom';
import { useLoaderData } from 'react-router';

function Profile() {
  const userData = useLoaderData() as UserInfo;
  const navigator = useNavigate();
  useEffect(() => {
    if (!userData) {
      navigator('/auth/login');
    }
  }, [userData, navigator]);

  const editProfile = () => {
    navigator('/auth/signup');
    return;
  };
  return (
    <div className="flex flex-col h-full">
      <h2 className="font-bold text-lg">내 정보</h2>
      <div className="w-full h-1/5 my-6 flex">
        <img className="w-32 h-40 rounded-2xl" src={userData?.image || '../public/default-profile.png'}></img>
        <div className="w-2/5 flex flex-col ml-6">
          <p className="font-bold text-md">
            {userData?.name} ({userData?.nickName})
          </p>
          <p className="text-sm mt-3 text-background-400">{userData?.email}</p>
          <p className="text-sm text-background-400">{userData?.phoneNum}</p>
          <Button text="수정" className="w-1/4 m-6 ml-0" onClick={editProfile}></Button>
        </div>
      </div>
      <hr className="border-background-300"></hr>
      <div className="w-full h-4/5 flex">
        <h2 className="font-bold text-lg mt-6">예약 내역</h2>
        <div></div>
      </div>
    </div>
  );
}

export default Profile;
