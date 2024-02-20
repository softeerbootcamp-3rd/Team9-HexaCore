import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import App from '@/App.tsx';
import NotFound from '@/pages/NotFound';
import authRoutes from '@/pages/auth/authRoutes';
import homeRoutes from '@/pages/home/homeRoutes';
import profileRoutes from '@/pages/profile/profileRoutes';
import carRoutes from '@/pages/cars/carRoutes';
import hostsRoutes from '@/pages/hosts/hostsRoutes';
import paymentRoutes from '@/pages/payment/paymentRoutes';
import '@/index.css';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      ...authRoutes,
      ...homeRoutes,
      ...profileRoutes,
      ...carRoutes,
      ...hostsRoutes,
      ...paymentRoutes,
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

