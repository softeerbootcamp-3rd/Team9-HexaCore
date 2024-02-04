import { Link } from 'react-router-dom';
import Envelope from '@/components/svgs/Envelope';
import UserCircle from '@/components/svgs/UserCircle';
import Button from '@/components/Button';

function Header() {
  return (
    <header className="fixed z-10 w-screen h-20 bg-white shadow-md flex items-center">
      <div className="container mx-auto h-12 flex items-center justify-between">
        <div className="flex-1"></div>
        <Link to="/">
          <img src="/Tayo-logo.png" alt="Tayo logo" className="h-12" />
        </Link>
        <div className="flex gap-x-1 items-center justify-end flex-1">
          <Link to="/hosts/manage" className="mx-2">
            <Button text="호스트" type="enabled" isRounded />
          </Link>
          <Link to="#" className="p-2 text-background-500 hover:text-background-900">
            <Envelope />
          </Link>
          <Link to="/profile" className="p-2 text-background-500 hover:text-background-900">
            <UserCircle />
          </Link>
        </div>
      </div>
    </header>
  );
}

export default Header;

