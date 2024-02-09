import Button from '@/components/Button';
import { useEffect } from 'react';
import { UserInfo } from '@/pages/profile/profileRoutes';
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
    <div className="flex h-full flex-col">
      <h2 className="text-lg font-bold">내 정보</h2>
      <div className="my-6 flex h-1/5 w-full">
        <img className="h-40 w-32 rounded-2xl" src={userData?.image || '../public/default-profile.png'}></img>
        <div className="ml-6 flex w-2/5 flex-col">
          <p className="text-md font-bold">
            {userData?.name} ({userData?.nickName})
          </p>
          <p className="mt-3 text-sm text-background-400">{userData?.email}</p>
          <p className="text-sm text-background-400">{userData?.phoneNum}</p>
          <Button text="수정" className="m-6 ml-0 w-1/4" onClick={editProfile}></Button>
        </div>
      </div>
      <hr className="border-background-300"></hr>
      <div className="flex h-4/5 w-full">
        <h2 className="mt-6 text-lg font-bold">예약 내역</h2>
        <div></div>
      </div>
    </div>
  );
}

export default Profile;

