import { useState, useRef, useEffect, ChangeEvent } from 'react';
import InputBox from '@/components/InputBox';
import Button from '@/components/Button';
import type { ResponseWithoutData } from '@/fetches/common/response.type';
import { server } from '@/fetches/common/axios';
import { useNavigate } from 'react-router-dom';
import { UserData } from '@/fetches/users/fetchUser';
import { useLoaderData } from 'react-router';

const emailPattern: RegExp = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
const passwordPattern: RegExp = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
const phoneNumberPattern: RegExp = /^\d{3}-\d{4}-\d{4}$/;

function SignUp() {
  const navigate = useNavigate();
  const data = useLoaderData() as UserData;

  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [profilePicture, setProfilePicture] = useState<string | null>(null);

  const emailInputRef = useRef<HTMLInputElement | null>(null);
  const [emailErr, setEmailErr] = useState('');
  const [isWrongEmail, setIsWrongEmail] = useState(false);

  const pwdInputRef = useRef<HTMLInputElement | null>(null);
  const [pwdErr, setPwdErr] = useState('');
  const [isWrongPassword, setIsWrongPassword] = useState(false);

  const nameInputRef = useRef<HTMLInputElement | null>(null);
  const [nameErr, setNameErr] = useState('');
  const [isWrongName, setIsWrongName] = useState(false);

  const phoneNumInputRef = useRef<HTMLInputElement | null>(null);
  const [phoneErr, setPhoneErr] = useState('');
  const [isWrongPhoneNum, setIsWrongPhoneNum] = useState(false);

  const setUserInfo = () => {
    if (phoneNumInputRef.current) {
      phoneNumInputRef.current.value = data.phoneNum;
    }
    if (nameInputRef.current) {
      nameInputRef.current.value = data.name;
      nameInputRef.current.readOnly = true;
    }
    if (emailInputRef.current) {
      emailInputRef.current.value = data.email;
      emailInputRef.current.readOnly = true;
    }
    if (pwdInputRef.current) {
      pwdInputRef.current.autocomplete = 'new-password';
    }
    if (fileInputRef.current) {
      fileInputRef.current.src = data.image;
    }
    setProfilePicture(data.image);
  };
  useEffect(() => {
    if (data) {
      setUserInfo();
    }
  }, []);

  const handleSignUp = async () => {
    const email: string = emailInputRef.current?.value || '';
    const password: string = pwdInputRef.current?.value || '';
    const name: string = nameInputRef.current?.value || '';
    const phoneNumber: string = phoneNumInputRef.current?.value || '';
    const profileImg: File | null = fileInputRef.current?.files?.[0] || null;

    if (checkEmail(email) && checkPwd(password) && checkName(name) && checkPhoneNum(phoneNumber)) {
      const formData = new FormData();
      formData.append('email', email);
      formData.append('password', password);
      formData.append('name', name);
      formData.append('phoneNumber', phoneNumber);
      if (profileImg !== null) {
        formData.append('profileImg', profileImg);
      }
      const response = await server.post<ResponseWithoutData>('/auth/signup', {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        data: formData,
      });
      if (response.success) {
        // 회원가입 성공시 로그인 페이지로
        navigate('/auth/login');
      } else {
        // 에러 메세지 띄우기
        setPhoneErr(response.message);
      }
    }
  };

  const handleUpdate = async () => {
    const email: string = emailInputRef.current?.value || '';
    const password: string = pwdInputRef.current?.value || '';
    const name: string = nameInputRef.current?.value || '';
    const phoneNumber: string = phoneNumInputRef.current?.value || '';
    const profileImg: File | null = fileInputRef.current?.files?.[0] || null;

    if (checkEmail(email) && checkPwd(password) && checkName(name) && checkPhoneNum(phoneNumber)) {
      const formData = new FormData();
      formData.append('email', email);
      formData.append('password', password);
      formData.append('name', name);
      formData.append('phoneNumber', phoneNumber);
      if (profileImg !== null) {
        formData.append('profileImg', profileImg);
      }
      const response = await server.patch<ResponseWithoutData>('/users', {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        data: formData,
      });

      if (response.success) {
        // 회원정보 수정 성공시 마이 페이지로
        navigate('/profile');
      } else {
        // TODO: 수정에 실패했음을 표시하기
      }
    }
  };

  // 이메일 형식에 맞는지 확인
  const checkEmail = (email: string): boolean => {
    if (emailPattern.test(email)) {
      setIsWrongEmail(false);
      setEmailErr('');
      return true;
    }
    setIsWrongEmail(true);
    setEmailErr('이메일 형식에 맞게 입력해주세요');
    return false;
  };

  // 비밀번호 형식에 맞는지 확인
  const checkPwd = (pwd: string): boolean => {
    if (data && !pwdInputRef.current?.value) {
      return true;
    }
    if (passwordPattern.test(pwd)) {
      setIsWrongPassword(false);
      setPwdErr('');
      return true;
    }
    setIsWrongPassword(true);
    setPwdErr('영어, 숫자, 기호 포함 길이 6이상 입력해주세요');
    return false;
  };

  // 이름 입력했는지 확인
  const checkName = (name: string): boolean => {
    if (name === '') {
      setIsWrongName(true);
      setNameErr('이름을 입력해주세요.');
      return false;
    }
    setIsWrongName(false);
    setNameErr('');
    return true;
  };

  // 전화번호 형식에 맞는지 확인
  const checkPhoneNum = (phoneNum: string): boolean => {
    if (phoneNumberPattern.test(phoneNum)) {
      setIsWrongPhoneNum(false);
      setPhoneErr('');
      return true;
    }
    setIsWrongPhoneNum(true);
    setPhoneErr('000-0000-0000 형식으로 입력해주세요.');
    return false;
  };

  const phoneNumChange = () => {
    if (phoneNumInputRef.current) {
      const phoneNumber = phoneNumInputRef.current.value;

      const phonePattern1: RegExp = /^\d{4}$/;
      const phonePattern2: RegExp = /^\d{3}-\d{5}$/;
      const phonePattern3: RegExp = /^\d{3}-\d{4}-\d{4}\d/;

      if (phonePattern1.test(phoneNumber)) {
        phoneNumInputRef.current.value = phoneNumber.replace(/(\d{3})(\d{1})/, '$1-$2');
      } else if (phonePattern2.test(phoneNumber)) {
        phoneNumInputRef.current.value = phoneNumber.replace(/(\d{3})-(\d{4})(\d)/, '$1-$2-$3');
      }

      // 마지막 문자가 숫자가 아니면 제거
      if (!/\d$/.test(phoneNumber.slice(-1)) || phonePattern3.test(phoneNumber)) {
        phoneNumInputRef.current.value = phoneNumber.slice(0, -1);
      }
    }
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

  const handleImgButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  return (
    <div className='flex justify-center'>
      <div className='flex w-5/12 flex-col'>
        <div className='pl-8 pt-6 text-[21px] font-semibold text-background-600'>{data === null ? '회원가입' : '회원정보 수정'}</div>

        <div className='flex justify-between pb-4'>
          <div className='w-44'></div>
          {profilePicture === null ? (
            <img src='/defaultProfile.png' className='h-32 w-32 rounded-full shadow-md' onClick={handleImgButtonClick} />
          ) : (
            <img src={profilePicture} className='h-32 w-32 rounded-full shadow-md' onClick={handleImgButtonClick} />
          )}
          <div className='flex flex-col justify-end pb-1 pr-4'>
            {profilePicture === null ? (
              <button onClick={handleImgButtonClick} className='h-10 w-24 rounded-full text-sm font-semibold text-primary-500 ring-2 ring-primary-500'>
                사진 등록
              </button>
            ) : (
              <Button text='삭제' onClick={() => setProfilePicture(null)} type='danger' isRounded className='h-10 w-24'></Button>
            )}
          </div>

          <input ref={fileInputRef} type='file' accept='image/*' id='profilePicture' onChange={handleImageUpload} className='hidden' />
        </div>

        <InputBox ref={emailInputRef} title='이메일' placeHolder='tayo@tayo.com' type='email' isWrong={isWrongEmail} errorMsg={emailErr} />
        <InputBox
          ref={pwdInputRef}
          title='비밀번호'
          placeHolder='영어, 숫자, 기호 포함 길이 6이상'
          type='password'
          isWrong={isWrongPassword}
          errorMsg={pwdErr}
        />
        <InputBox ref={nameInputRef} title='이름' placeHolder='김타요' isWrong={isWrongName} errorMsg={nameErr} />
        <InputBox ref={phoneNumInputRef} title='전화번호' placeHolder='010-1234-5678' isWrong={isWrongPhoneNum} errorMsg={phoneErr} onChange={phoneNumChange} />

        <div className='flex justify-end pb-10 pr-3 pt-7'>
          {data === null ? (
            <Button text='회원가입' className='h-12 w-36' isRounded onClick={handleSignUp} />
          ) : (
            <Button text='수정' className='h-12 w-36' isRounded onClick={handleUpdate} />
          )}
        </div>
      </div>
    </div>
  );
}

export default SignUp;
