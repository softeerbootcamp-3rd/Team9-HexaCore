import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import App from '@/App.tsx';
import NotFound from '@/pages/NotFound';
import Home from '@/pages/Home.jsx';
import CarDetail from '@/pages/CarDetail';
import HostManage from '@/pages/HostManage';
import HostRegister from '@/pages/HostRegister';
import Profile from '@/pages/Profile';
import '@/index.css';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: '/',
        element: <Home />,
      },
      {
        path: 'cars/:carId',
        element: <CarDetail />,
      },
      {
        path: 'hosts/manage',
        element: <HostManage />,
      },
      {
        path: 'hosts/register',
        element: <HostRegister />,
      },
      {
        path: 'profile/:userId',
        element: <Profile />,
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);

