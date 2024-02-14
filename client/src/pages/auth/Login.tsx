import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import InputBox from '@/pages/auth/InputBox';
import Button from '@/components/Button';
import type { ResponseWithoutData, ResponseWithData, ResponseWithPagination } from "@/fetches/common/response.type";
import { server } from "@/fetches/common/axios";

function Login() {
  const emailInputRef = useRef<HTMLInputElement | null>(null);
  const pwdInputRef = useRef<HTMLInputElement | null>(null);

  const [wrongEmail, setWrongEmail] = useState(false);
  const [emailErr, setEmailErr] = useState("");

  const [wrongPwd, setWrongPwd] = useState(false);
  const [pwdErr, setPwdErr] = useState("");

  const handleLogin = async () => {
    const userEmail: string = emailInputRef.current?.value || '';
    const userPwd: string = pwdInputRef.current?.value || '';

    if (!checkEmailEmpty(userEmail) && !checkPwdEmpty(userPwd)) {
      const response = await server.post<ResponseWithoutData>('/auth/login', {
        data: {
          email: userEmail,
          password: userPwd,
        }
      });
      if (response.success) {
        window.location.href = '/';
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
    <div className="flex justify-center pt-16">
      <div className="flex w-5/12 flex-col">
        <div className={`p-8 text-2xl`}>로그인</div>

        <InputBox 
          ref={emailInputRef} 
          title="이메일" 
          placeHolder="이메일을 입력해주세요." 
          type="email" 
          isWrong={wrongEmail}
          errorMsg={emailErr}
        />
        <InputBox 
          ref={pwdInputRef} 
          title="비밀번호" 
          placeHolder="비밀번호를 입력해주세요." 
          type="password" 
          isWrong={wrongPwd}
          errorMsg={pwdErr}
        />

        <div className="flex justify-end pr-14 pt-8">
          <Link to="/auth/signup" className="pr-8">
            <Button text="회원가입 하러 가기" className="h-12 w-48 bg-primary-300 hover:bg-primary-400" isRounded></Button>
          </Link>
          <Button text="로그인" className="h-12 w-36" isRounded onClick={handleLogin} />
        </div>
      </div>
    </div>
  );
}

export default Login;

