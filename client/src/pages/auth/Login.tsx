import { useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import InputBox from '@/components/InputBox';
import Button from '@/components/Button';
import type { ResponseWithData } from '@/fetches/common/response.type';
import { server } from '@/fetches/common/axios';
import { LoginResponse } from '@/fetches/auth/auth.type';
import { useAuth } from '@/contexts/AuthContext';

function Login() {
  const { setAuth } = useAuth();
  const navigate = useNavigate();
  const emailInputRef = useRef<HTMLInputElement | null>(null);
  const pwdInputRef = useRef<HTMLInputElement | null>(null);

  const [isWrongEmail, setWrongEmail] = useState(false);
  const [emailErr, setEmailErr] = useState('');

  const [isWrongPwd, setWrongPwd] = useState(false);
  const [pwdErr, setPwdErr] = useState('');

  const handleLogin = async () => {
    const userEmail: string = emailInputRef.current?.value ?? '';
    const userPwd: string = pwdInputRef.current?.value ?? '';

    if (!checkEmailEmpty(userEmail) && !checkPwdEmpty(userPwd)) {
      const response = await server.post<ResponseWithData<LoginResponse>>('/auth/login', {
        data: {
          email: userEmail,
          password: userPwd,
        },
      });
      if (response.success) {
        setAuth({
          accessToken: response.data.tokens.accessToken,
          refreshToken: response.data.tokens.refreshToken,
          userId: response.data.loginUserInfo.userId,
        });
        navigate('/');
      } else {
        // TODO: 로그인 실패 시 처리
        setPwdErr('올바른 이메일과 비밀번호를 입력해주세요.');
      }
    }
  };

  // 이메일 입력란 비어있는지 확인
  const checkEmailEmpty = (email: string): boolean => {
    if (email === '') {
      setWrongEmail(true);
      setEmailErr('이메일을 입력해주세요.');
      return true;
    }

    setWrongEmail(false);
    setEmailErr('');
    return false;
  };

  // 비밀번호 입력란 비어있는지 확인
  const checkPwdEmpty = (pwd: string): boolean => {
    if (pwd === '') {
      setWrongPwd(true);
      setPwdErr('비밀번호를 입력해주세요.');
      return true;
    }

    setWrongPwd(false);
    setPwdErr('');
    return false;
  };

  return (
    <div className='flex justify-center pt-24'>
      <div className='flex w-5/12 flex-col'>
        <div className='pb-7 pl-5 text-[21px] font-semibold text-background-600'>로그인</div>

        <InputBox ref={emailInputRef} title='이메일' placeHolder='이메일을 입력해주세요.' type='email' isWrong={isWrongEmail} errorMsg={emailErr} />
        <InputBox ref={pwdInputRef} title='비밀번호' placeHolder='비밀번호를 입력해주세요.' type='password' isWrong={isWrongPwd} errorMsg={pwdErr} />

        <div className='flex justify-end pr-3 pt-8'>
          <Link to='/auth/signup' className='pr-8'>
            <Button text='회원가입 하러 가기' className='h-12 w-44' isRounded></Button>
          </Link>
          <Button text='로그인' className='h-12 w-32' isRounded onClick={handleLogin} />
        </div>
      </div>
    </div>
  );
}

export default Login;

