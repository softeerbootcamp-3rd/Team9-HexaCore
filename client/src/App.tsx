import { Outlet } from 'react-router-dom';
import Header from '@/components/Header';

function App() {
  return (
    <div className="flex bg-background-100">
      <Header />
      <main className="container mx-auto min-h-screen grow pt-28">
        <Outlet />
      </main>
    </div>
  );
}

export default App;

