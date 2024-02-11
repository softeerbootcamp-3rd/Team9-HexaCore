import { useState, useRef, ChangeEvent } from 'react';
import InputBox from '@/pages/auth/InputBox';
import Button from '@/components/Button';

const emailPattern: RegExp = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
const passwordPattern: RegExp = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
const phoneNumberPattern: RegExp = /^\d{3}-\d{4}-\d{4}$/;

function SignUp() {
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [profilePicture, setProfilePicture] = useState<string | null>(null);

  const emailInputRef = useRef<HTMLInputElement | null>(null);
  const [isValidEmail, setIsValidEmail] = useState(false);

  const pwdInputRef = useRef<HTMLInputElement | null>(null);
  const [isValidPassword, setIsValidPassword] = useState(false);

  const nameInputRef = useRef<HTMLInputElement | null>(null);
  const [isValidName, setIsValidName] = useState(false);

  const nicknameInputRef = useRef<HTMLInputElement | null>(null);
  const [isValidNickname, setIsValidNickname] = useState(false);

  const phoneNumInputRef = useRef<HTMLInputElement | null>(null);
  const [isValidPhoneNum, setIsValidPhoneNum] = useState(false);

  const handleSignUp = () => {
    // 서버에 회원가입 요청
  };

  const handleImageUpload = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];

    if (file) {
      const reader = new FileReader();

      reader.onloadend = () => {
        setProfilePicture(reader.result as string);
      };

      reader.readAsDataURL(file);
    }
  };

  const handleButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const checkInputValues = (): boolean => {
    return isValidEmail && isValidPassword && isValidName && isValidNickname && isValidPhoneNum;
  };

  return (
    <div className="flex justify-center">
      <div className="flex w-5/12 flex-col">
        <div className="p pl-8 pt-5 text-2xl">회원가입</div>

        <div className="flex justify-between pb-4">
          <div className="w-44"></div>
          {profilePicture === null ? (
            <img src="/defaultProfile.png" className="h-32 w-32 rounded-full shadow-md" onClick={handleButtonClick} />
          ) : (
            <img src={profilePicture} className="h-32 w-32 rounded-full shadow-md" onClick={handleButtonClick} />
          )}
          <div className="flex flex-col justify-end pb-2 pr-10">
            {profilePicture === null ? (
              <button onClick={handleButtonClick} className="h-10 w-24 rounded-full text-sm font-semibold text-primary-500 ring-2 ring-primary-500">
                사진 등록
              </button>
            ) : (
              <Button text="삭제" onClick={() => setProfilePicture(null)} type="danger" isRounded className="h-10 w-24"></Button>
            )}
          </div>

          <input ref={fileInputRef} type="file" accept="image/*" id="profilePicture" onChange={handleImageUpload} className="hidden" />
        </div>

        <InputBox
          ref={emailInputRef}
          title="이메일"
          placeHolder="tayo@tayo.com"
          type="email"
          onBlur={() => {
            if (!emailInputRef.current) return;
            setIsValidEmail(emailPattern.test(emailInputRef.current.value));
          }}
        />
        <InputBox
          ref={pwdInputRef}
          title="비밀번호"
          placeHolder="영어, 숫자, 기호 포함 길이 6이상 비밀번호"
          type="password"
          onBlur={() => {
            if (!pwdInputRef.current) return;
            setIsValidPassword(passwordPattern.test(pwdInputRef.current.value));
          }}
        />
        <InputBox
          ref={nameInputRef}
          title="이름"
          placeHolder="김타요"
          onBlur={() => {
            setIsValidName(nameInputRef.current?.value !== '');
          }}
        />
        <InputBox
          ref={nicknameInputRef}
          title="닉네임"
          placeHolder="타요 닉네임"
          onBlur={() => {
            setIsValidNickname(nicknameInputRef.current?.value !== '');
          }}
        />
        <InputBox
          ref={phoneNumInputRef}
          title="전화번호"
          placeHolder="010-1234-5678"
          onBlur={() => {
            if (!phoneNumInputRef.current) return;
            setIsValidPhoneNum(phoneNumberPattern.test(phoneNumInputRef.current.value));
          }}
        />

        <div className="flex justify-end pb-10 pr-14 pt-8">
          <Button text="회원가입" className="h-12 w-44" type={checkInputValues() ? 'enabled' : 'disabled'} isRounded onClick={handleSignUp} />
        </div>
      </div>
    </div>
  );
}

export default SignUp;

