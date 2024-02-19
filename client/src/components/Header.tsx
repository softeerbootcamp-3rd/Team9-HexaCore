import { Link } from 'react-router-dom';
import Envelope from '@/components/svgs/Envelope';
import UserCircle from '@/components/svgs/UserCircle';
import Button from '@/components/Button';
import { logout } from '@/fetches/auth/fetchAuth';

function Header() {
  const isLogin = localStorage.getItem("accessToken") !== null ? true : false;

  return (
    <header className="fixed z-10 flex h-20 w-screen items-center bg-white shadow-md">
      <div className="container mx-auto flex h-12 items-center justify-between">
        <div className="flex-1"></div>
        <Link to="/">
          <img src="/Tayo-logo.png" alt="Tayo logo" className="h-12" />
        </Link>
        <div className="flex flex-1 items-center justify-end gap-x-8">

          {
            (isLogin) 
            ?
            <button className="text-sm mr-1 text-primary-500 hover:text-primary-600" onClick={logout}>
              로그아웃
            </button> 
            :
            <Link to="/auth/login" className="text-sm mr-2 text-primary-500 hover:text-primary-600">
              <Button text="로그인" type="enabled" isRounded />
            </Link>
          }

          <Link to="/hosts/manage" className={`
          mr-2 ${isLogin ? '' : 'hidden'}`}>
            <Button text="호스트" type="enabled" isRounded />
          </Link>

          <button className="p-2 text-background-500 hover:text-background-900">
            <Envelope />
          </button>

          <Link to="/profile" className="p-2 text-background-500 hover:text-background-900">
            <UserCircle />
          </Link>

        </div>
      </div>
    </header>
  );
}

export default Header;

