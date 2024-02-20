import { Link, useNavigate } from 'react-router-dom';
import UserCircle from '@/components/svgs/UserCircle';
import Button from '@/components/Button';
import { useAuth } from '@/contexts/AuthContext';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import { useEffect, useState } from 'react';
import { NotificationData } from '@/fetches/notification/notification.type';
import { EventSourcePolyfill } from 'event-source-polyfill';
import BellIcon from './svgs/BellIcon';
import NotificationBox from './notification/NotificationBox';
import { getNotifications } from '@/fetches/notification/fetchNotification';

function Header() {
  const navigate = useNavigate();
  const { auth, setAuth } = useAuth();
  const isLogin = auth.accessToken !== null;

  // 알림창을 보여줄지 boolean 값
  const [showNoti, setShowNoti] = useState(false);
  // 현재 수신한 알림 개수
  const [notiCnt, setNotiCnt] = useState(0);
  // 알림 list
  const [notificationList, setNotificationList] = useState<NotificationData[]>([]);

  useEffect(() => {
    // 로그인한 유저인 경우
    if (isLogin) {

      // 서버에 저장되어 있는 알림을 모두 읽고, NotiBox 세팅
      getAllNotification();

      const token = 'Bearer ' + auth.accessToken;

      // 서버에서 보내주는 sse 를 구독
      const eventSource = new EventSourcePolyfill("http://localhost:8080/api/v1/notifications/subscribe", {
        headers: {
          Authorization: token
        },
      });

      // sse 이벤트 핸들러 등록
      eventSource.addEventListener('sse', event => {
        notificationEventHandler(event);
      });
    }
  }, []);

  const notificationEventHandler = (event: any) => {
    // 알림 숫자를 1 증가, 배열 맨 앞에 방금 수신한 알림 추가
    console.log(event);
    setNotiCnt((prevCnt: number) => {
      const cnt = prevCnt + 1;
      return cnt;
    });
    // todo 모달로 온 메세지를 띄워주기
  };

  const logout = async () => {
    const response = await server.get<ResponseWithoutData>('/auth/logout');
    if (!response.success) {
      alert('로그아웃 실패'); // TODO: 실패 시 처리
      return;
    }

    setAuth({
      userId: null,
      accessToken: null,
      refreshToken: null,
    });
    navigate('/');
  };

  // 알림 버튼을 클릭했을 때 실행하는 메소드
  const checkNotificationBox = async () => {
    setShowNoti(!showNoti); 
  };

  const getAllNotification = async () => {
    // 디비에 저장되어 있는 알림들 읽어오기
    const response = await getNotifications();

    if (response !== undefined) {
      setNotificationList(response);
      setNotiCnt(response?.length);
    } else {
      setNotificationList([]);
      setNotiCnt(0);
    }
  };

  return (
    <header className="fixed z-10 flex h-20 w-screen items-center bg-white shadow-md">
      <div className="container mx-auto flex h-12 items-center justify-between">
        <div className="flex-1"></div>
        <Link to="/">
          <img src="/Tayo-logo.png" alt="Tayo logo" className="h-12" />
        </Link>
        <div className="flex flex-1 items-center justify-end gap-x-8">

          {
            (localStorage.getItem("userId")) 
            ?
            <button className="text-sm mr-1 text-primary-500 hover:text-primary-600" onClick={logout}>
              로그아웃
            </button> 
            :
            <Link to="/auth/login" className="text-sm mr-2 text-primary-500 hover:text-primary-600">
              <Button text="로그인" type="enabled" isRounded />
            </Link>
          }

          <Link to="/hosts/manage" className={`
          mr-2 ${(localStorage.getItem("userId"))  ? '' : 'hidden'}`}>
            <Button text="호스트" type="enabled" isRounded />
          </Link>

          <div className='relative transition-opacity'>
            <button className="p-2 text-background-500 hover:text-background-900" onClick={checkNotificationBox}>
              <BellIcon />
            </button>

            <button className={`${(notiCnt !== 0) ? "" : "hidden"}
            flex items-center justify-center absolute top-0 right-0 bg-primary-300 rounded-full w-[19px] h-[19px] text-center text-[13px] text-white`}
            >
              {notiCnt}
            </button>

            <NotificationBox
              items={notificationList ?? []}
              hidden={showNoti}
              setHidden={() => {setShowNoti(false)}}
            />
          </div>

          <Link to="/profile" className="p-2 text-background-500 hover:text-background-900">
            <UserCircle />
          </Link>

        </div>
      </div>
    </header>
  );
}

export default Header;

