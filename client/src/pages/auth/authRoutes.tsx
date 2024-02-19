import type { RouteObject } from 'react-router-dom';
import Login from '@/pages/auth/Login';
import SignUp from '@/pages/auth/SignUp';
import { fetchUser, parseUser } from '@/fetches/users/fetchUser';

interface LoaderParams {
  params: {
    userId?: string;
  };
}

const authRoutes: RouteObject[] = [
  {
    path: 'auth/login',
    element: <Login />,
  },
  {
    path: 'auth/signup/:userId',
    loader: async ({ params }: LoaderParams) => {
	  const userId = params.userId ?? localStorage.getItem("userId") ?? null;
	  var data = null;
	  if(userId){
		const [userResult] = await Promise.allSettled([fetchUser(parseInt(userId))]);
		if (userResult.status == 'fulfilled' && userResult.value != undefined){
			if(userResult.value.code === 200){
			  data = parseUser(userResult.value.data);
			}
			else{
			  throw Error("예기치 못한 오류가 발생했습니다.")
			}
		  }
	  }
      return data;
    },
    element: <SignUp />,
  },
];

export default authRoutes;
