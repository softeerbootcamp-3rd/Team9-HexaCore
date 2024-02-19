import { Link, useNavigate } from 'react-router-dom';
import Envelope from '@/components/svgs/Envelope';
import UserCircle from '@/components/svgs/UserCircle';
import Button from '@/components/Button';
import { useAuth } from '@/contexts/AuthContext';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';

function Header() {
  const navigate = useNavigate();
  const { auth, setAuth } = useAuth();
  const isLogin = auth.userId !== null;
  console.log('auth', auth);

  const logout = async () => {
    const response = await server.get<ResponseWithoutData>('/auth/logout');
    if (!response.success) {
      alert('로그아웃 실패'); // TODO: 실패 시 처리
      return;
    }

    setAuth({
      userId: null,
      accessToken: null,
      refreshToken: null,
    });
    navigate('/');
  };

  return (
    <header className='fixed z-10 flex h-20 w-screen items-center bg-white shadow-md'>
      <div className='container mx-auto flex h-12 items-center justify-between'>
        <div className='flex-1'></div>
        <Link to='/'>
          <img src='/Tayo-logo.png' alt='Tayo logo' className='h-12' />
        </Link>
        <div className='flex flex-1 items-center justify-end gap-x-8'>
          {isLogin ? (
            <button className='mr-1 text-sm text-primary-500 hover:text-primary-600' onClick={logout}>
              로그아웃
            </button>
          ) : (
            <Link to='/auth/login' className='mr-2 text-sm text-primary-500 hover:text-primary-600'>
              <Button text='로그인' type='enabled' isRounded />
            </Link>
          )}

          <Link
            to='/hosts/manage'
            className={`
          mr-2 ${isLogin ? '' : 'hidden'}`}>
            <Button text='호스트' type='enabled' isRounded />
          </Link>

          <button className='p-2 text-background-500 hover:text-background-900'>
            <Envelope />
          </button>

          <Link to='/profile' className='p-2 text-background-500 hover:text-background-900'>
            <UserCircle />
          </Link>
        </div>
      </div>
    </header>
  );
}

export default Header;

