import { useRef } from 'react';
import { Link } from 'react-router-dom';
import InputBox from "@/pages/auth/InputBox";
import Button from '@/components/Button';

function Login() {
	const emailInputRef = useRef<HTMLInputElement | null>(null);
	const pwdInputRef = useRef<HTMLInputElement | null>(null);

	const handleLogin = () => {
		let userEmail = emailInputRef.current?.value;
		let userPwd = pwdInputRef.current?.value;
		if (userEmail == '') {
			alert('아이디를 입력해주세요');
		} else if (userPwd == '') {
			alert('비밀번호를 입력해주세요');
		} else {
			// 서버에 로그인 요청
		}
	};

	return (
		<div className='flex justify-center pt-16'>
			<div className='flex flex-col w-5/12'>

				<div className={`text-2xl p-8`}>로그인</div>

				<InputBox
					ref={emailInputRef}
					title='아이디'
					placeHolder='아이디를 입력해주세요.'
					type='email'
				/>
				<InputBox
					ref={pwdInputRef}
					title='비밀번호'
					placeHolder='비밀번호를 입력해주세요.'
					type='password'
				/>

				<div className='flex pt-8 justify-end pr-14'>
					<Link to='/auth/signup' className='pr-8'>
						<Button text='회원가입 하러 가기' className='w-48 h-12 bg-primary-300 hover:bg-primary-400' isRounded></Button>
					</Link>
					<Button text='로그인' className='w-36 h-12' isRounded onClick={handleLogin} />
				</div>

			</div>
		</div>
	);
}

export default Login;

