import { Link } from 'react-router-dom';
import Envelope from '@/components/svgs/Envelope';
import UserCircle from '@/components/svgs/UserCircle';
import Button from '@/components/Button';

function Header() {
  return (
    <header className="fixed z-10 flex h-20 w-screen items-center bg-white shadow-md">
      <div className="container mx-auto flex h-12 items-center justify-between">
        <div className="flex-1"></div>
        <Link to="/">
          <img src="/Tayo-logo.png" alt="Tayo logo" className="h-12" />
        </Link>
        <div className="flex flex-1 items-center justify-end gap-x-1">
          <Link to="/hosts/manage" className="mx-2">
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

