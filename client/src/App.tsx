import { Outlet } from 'react-router-dom';
import Header from '@/components/Header';

function App() {
  return (
    <div className="bg-background-100">
      <Header />
      <main className="container mx-auto pt-28 min-h-screen">
        <Outlet />
      </main>
    </div>
  );
}

export default App;

