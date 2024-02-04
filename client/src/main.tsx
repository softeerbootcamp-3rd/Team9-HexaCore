import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import App from '@/App.tsx';
import NotFound from '@/pages/NotFound';
import homeRoutes from '@/pages/home/homeRoutes';
import profileRoutes from '@/pages/profile/profileRoutes';
import carRoutes from '@/pages/cars/carRoutes';
import hostsRoutes from '@/pages/hosts/hostsRoutes';
import '@/index.css';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      ...homeRoutes,
      ...profileRoutes,
      ...carRoutes,
      ...hostsRoutes,
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

