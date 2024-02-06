import { useState, useRef, ChangeEvent } from 'react';
import InputBox from '@/pages/auth/InputBox';
import Button from '@/components/Button';

function SignUp() {
	const [userId, setUserId] = useState('');
	const [password, setPassword] = useState('');
	const [name, setName] = useState('');
	const [nickname, setNickname] = useState('');
	const [email, setEmail] = useState('');
	const [profilePicture, setProfilePicture] = useState<string | null>(null);
	const fileInputRef = useRef<HTMLInputElement | null>(null);

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


	return (
		<div className='flex justify-center'>
			<div className='flex flex-col w-5/12'>

				<text className='text-2xl pl-8 pt-5 p'>회원가입</text>

				<div className="flex justify-between pb-4">
					<div className='w-44'></div>
					{
						profilePicture === null ?
							<img src="/defaultProfile.png" className="rounded-full w-32 h-32" onClick={handleButtonClick} /> :
							<img src={profilePicture} className="rounded-full w-32 h-32" onClick={handleButtonClick} />
					}
					<div className='flex flex-col justify-end pb-2 pr-10'>
						{
							profilePicture === null ?
								<button onClick={handleButtonClick} className='rounded-full w-24 h-10 ring-2 ring-primary-500 font-semibold text-sm text-primary-500'>사진 등록</button> :
								<Button text='삭제' onClick={() => setProfilePicture(null)} type='danger' isRounded className='w-24 h-10'></Button>
						}
					</div>
					<input
						ref={fileInputRef}
						type="file"
						accept="image/*"
						id="profilePicture"
						onChange={handleImageUpload}
						className="hidden"
					/>
				</div>

				<InputBox
					title='아이디'
					placeHolder='아이디를 입력해주세요.'
					value={userId}
					onChange={(e) => setUserId(e.target.value)}
				/>
				<InputBox
					title='비밀번호'
					placeHolder='비밀번호를 입력해주세요.'
					type='password'
					value={password}
					onChange={(e) => setPassword(e.target.value)}
				/>
				<InputBox
					title='이름'
					placeHolder='이름을 입력해주세요.'
					value={name}
					onChange={(e) => setName(e.target.value)}
				/>
				<InputBox
					title='닉네임'
					placeHolder='닉네임을 입력해주세요.'
					value={nickname}
					onChange={(e) => setNickname(e.target.value)}
				/>
				<InputBox
					title='이메일'
					placeHolder='이메일을 입력해주세요.'
					type='email'
					value={email}
					onChange={(e) => setEmail(e.target.value)}
				/>

				<div className='flex pt-8 justify-end pr-14 pb-10'>
					<Button text='회원가입' className='w-44 h-12' isRounded onClick={handleSignUp} />
				</div>

			</div>
		</div>
	);
}

export default SignUp;

