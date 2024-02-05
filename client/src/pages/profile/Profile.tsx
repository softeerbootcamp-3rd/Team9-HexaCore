import { useLoaderData } from 'react-router';

function Profile() {
  const userId = useLoaderData() as string;

  return (
    <div>
      <h2>/profile/:userId?</h2>
      <div>userId: {userId}</div>
      <p>본인 혹은 다른 사용자의 정보를 볼 수 있는 화면 입니다.</p>
    </div>
  );
}

export default Profile;

