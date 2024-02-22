import { NotificationData } from "@/fetches/notification/notification.type";
import { useEffect, useState } from "react"
import { useAuth } from "@/contexts/AuthContext";
import { EventSourcePolyfill } from "event-source-polyfill";
import { server } from "@/fetches/common/axios";
import { ResponseWithData, ResponseWithoutData } from "@/fetches/common/response.type";
import BellIcon from "../svgs/BellIcon";
import NotificationItem from "./NotificationItem";

const baseURL = import.meta.env.MODE === 'production' ? import.meta.env.BASE_URL : 'http://localhost:8080';
const apiPrefix = import.meta.env.VITE_API_PREFIX ?? '/';

export default function NotificationBox() {
  const [showBox, setShowBox] = useState(false);
  const [notificationArray, setNotificationArray] = useState<NotificationData[]>([]);
  const { auth } = useAuth();

  const onDeleteAll = async () => { 
    if (notificationArray.length > 0) {
      const response = await server.delete<ResponseWithoutData>('/notifications');
    
      if (response.success) {
        setNotificationArray([]);
        setShowBox(false);
      } else {
        alert(response.message);
      }
    }
  };

  const onDeleteOne = async (notificationId: number) => { 
    const response = await server.delete<ResponseWithoutData>(`/notifications/${notificationId}`);
    
    if (response.success) {
      setNotificationArray((prevList: NotificationData[]) => {
        return prevList.filter((item) => item.id !== notificationId);
      });
    } else {
      alert(response.message);
    }
  };

  useEffect(() => {
    if (auth.userId) {

      const getNotifications = async () => {
        const response = await server.get<ResponseWithData<NotificationData[]>>('/notifications');
      
        if (response.success) {
          setNotificationArray(response.data);
        } else {
          alert(response.message)
          setNotificationArray([]);
        }
      };
      
      getNotifications();

      const eventSource = new EventSourcePolyfill(`${baseURL}${apiPrefix}/notifications/subscribe`, {
        headers: {
          Authorization: 'Bearer ' + auth.accessToken
        },
        heartbeatTimeout: 1000 * 60 * 60
      });

      eventSource.addEventListener('message', (event) => {
        if(event.data === 'Connected') return;
        const newNotification: NotificationData = JSON.parse(event.data);
          
        if (newNotification.title === '') return;
        setNotificationArray((prevList) => [newNotification, ...prevList]);
      })
    }
  }, []);


  return (
    <div className='relative transition-opacity'>

      <button className="p-2 text-background-500 hover:text-background-900" onClick={() => setShowBox(!showBox)}>
        <BellIcon />
      </button>

      <button className={`${(notificationArray.length === 0) ? "scale-0 opacity-0 pointer-events-none" : "scale-100 opacity-100"}
        flex items-center justify-center absolute top-0 right-0 bg-primary-300 rounded-full w-[19px] h-[19px] text-center text-[13px] text-white
        transition duration-300 ease-in-out`}
      >
        {notificationArray.length}
      </button>

      <div className={`${(showBox) ? "scale-100 opacity-100" : "scale-0 opacity-0 pointer-events-none"}
        flex flex-col absolute top-[42px] right-[-70px] border-solid border-[1px] border-background-200 mt-2 w-[400px] max-h-[450px] rounded-2xl shadow-md bg-white
        transition duration-300 ease-in-out`}
      >
        
        <div className="flex flex-row justify-between mb-5 pt-5 px-6">

          <div className="text-[19px] text-background-600 font-[600]">
            알림
          </div>

          <div className="flex flex-row">
            <div className={`
              text-[14px] text-background-400 pr-5 
              ${(notificationArray.length !== 0) ? 'hidden' : ''}
            `}>
              알림이 없습니다.
            </div>
            <div className="text-[14px] text-background-500">            
              {
                (notificationArray.length !== 0) ? 
                <button onClick={onDeleteAll}>
                  모두 삭제 
                </button>
                : 
                <button onClick={() => setShowBox(false)}>
                  닫기 
                </button>
              }
            </div>
          </div>
          
        </div>

        <div className="overflow-y-auto">
          {notificationArray.map((notification) => (
            <NotificationItem 
              key={notification.id} 
              item={notification}
              onClick={() => onDeleteOne(notification.id)}
            />
          ))}
        </div>

    </div>
  </div>
  );
}

