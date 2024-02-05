import { Outlet } from 'react-router-dom';
import Header from '@/components/Header';

function App() {
  return (
    <div>
      <Header />
      <div className="container mx-auto pt-28">
        <Outlet />
      </div>
    </div>
  );
}

export default App;

