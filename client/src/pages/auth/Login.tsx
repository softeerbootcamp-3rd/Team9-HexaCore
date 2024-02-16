import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import InputBox from '@/components/InputBox';
import Button from '@/components/Button';
import type { ResponseWithData } from "@/fetches/common/response.type";
import { server } from "@/fetches/common/axios";
import { useNavigate } from 'react-router-dom';
import { LoginResponse } from '@/fetches/auth/auth.type';

function Login() {
  const navigate = useNavigate();

  const emailInputRef = useRef<HTMLInputElement | null>(null);
  const pwdInputRef = useRef<HTMLInputElement | null>(null);

  const [isWrongEmail, setWrongEmail] = useState(false);
  const [emailErr, setEmailErr] = useState("");

  const [isWrongPwd, setWrongPwd] = useState(false);
  const [pwdErr, setPwdErr] = useState("");

  const handleLogin = async () => {
    const userEmail: string = emailInputRef.current?.value ?? '';
    const userPwd: string = pwdInputRef.current?.value ?? '';

    if (!checkEmailEmpty(userEmail) && !checkPwdEmpty(userPwd)) {
      const response = await server.post<ResponseWithData<LoginResponse>>('/auth/login', {
        data: {
          email: userEmail,
          password: userPwd,
        }
      });
      if (response.success) {
        localStorage.setItem("accessToken", response.data.tokens.accessToken);
        localStorage.setItem("refreshToken", response.data.tokens.refreshToken);
        navigate("/");
      } else {
        setPwdInputErr("올바른 이메일과 비밀번호를 입력해주세요.");
      }
    }
  };
  
  // 이메일 입력란 비어있는지 확인
  const checkEmailEmpty = (email: string): boolean => {
    if (email === '') {
      setWrongEmail(true);
      setEmailErr("이메일을 입력해주세요.");
      return true;
    }

    setWrongEmail(false);
    setEmailErr("")
    return false;
  };

  // 비밀번호 입력란 비어있는지 확인
  const checkPwdEmpty = (pwd: string): boolean => {
    if (pwd === '') {
      setPwdInputErr("비밀번호를 입력해주세요.");
      return true;
    }

    setWrongPwd(false);
    setPwdErr("")
    return false;
  };

  const setPwdInputErr = (errorMsg: string): void => {
    setWrongPwd(true);
    setPwdErr(errorMsg)
  }

  return (
    <div className="flex justify-center pt-24">
      <div className="flex w-5/12 flex-col">
        <div className="pl-5 pb-7 text-[21px] text-background-600 font-semibold">로그인</div>

        <InputBox 
          ref={emailInputRef} 
          title="이메일" 
          placeHolder="이메일을 입력해주세요." 
          type="email" 
          isWrong={isWrongEmail}
          errorMsg={emailErr}
        />
        <InputBox 
          ref={pwdInputRef} 
          title="비밀번호" 
          placeHolder="비밀번호를 입력해주세요." 
          type="password" 
          isWrong={isWrongPwd}
          errorMsg={pwdErr}
        />

        <div className="flex justify-end pr-3 pt-8">
          <Link to="/auth/signup" className="pr-8">
            <Button text="회원가입 하러 가기" className="h-12 w-44" isRounded></Button>
          </Link>
          <Button text="로그인" className="h-12 w-32" isRounded onClick={handleLogin} />
        </div>
      </div>
    </div>
  );
}

export default Login;

