import { PropsWithChildren, createContext, useContext, useEffect, useState } from 'react';

type AuthContextUser = {
  userId: number | null;
  accessToken: string | null;
  refreshToken: string | null;
};

type AuthContextValue = {
  auth: AuthContextUser;
  setAuth: (auth: AuthContextUser) => void;
};

const AuthContext = createContext<AuthContextValue>({
  auth: {
    userId: null,
    accessToken: null,
    refreshToken: null,
  },
  setAuth: () => {},
});

export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }: PropsWithChildren) {
  const [auth, setAuth] = useState<AuthContextUser>({
    userId: localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null,
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
  });

  useEffect(() => {}, []);

  const setAuthHandler = (auth: AuthContextUser) => {
    localStorage.setItem('accessToken', auth.accessToken ?? '');
    localStorage.setItem('refreshToken', auth.refreshToken ?? '');
    localStorage.setItem('userId', String(auth.userId ?? ''));
    setAuth(auth);
  };

  return (
    <AuthContext.Provider
      value={{
        auth,
        setAuth: setAuthHandler,
      }}>
      {children}
    </AuthContext.Provider>
  );
}

