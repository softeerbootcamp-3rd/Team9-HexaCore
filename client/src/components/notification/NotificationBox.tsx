import NotificationItem from "./NotificationItem";
import { NotificationData } from "../../fetches/notification/notification.type";
// import { deleteAllNotifications } from "@/fetches/notification/fetchNotification";
import { MouseEventHandler } from "react";

type Props = {
  text?: string;
  className?: string;
  items: NotificationData[];
  hidden: boolean;
  setHidden: MouseEventHandler<HTMLButtonElement>;
};

function NotificationBox({ className, items=[], hidden, setHidden }: Props) {
  return (
    <div 
      className={`
        ${hidden ? "scale-100 opacity-100" : "scale-0 opacity-0 pointer-events-none"}
        flex flex-col absolute top-[42px] right-[-70px] border-solid border-[1px] border-background-200 mt-2 w-[400px] max-h-[450px] rounded-2xl shadow-md bg-white
        transition duration-300 ease-in-out
      `}
    >
        
        <div className="flex flex-row justify-between mb-5 pt-5 px-6">

          <div className="text-[19px] text-background-600 font-[600]">
            알림
          </div>

          <div className="flex flex-row">
            <div className={`
              text-[14px] text-background-400 pr-5 
              ${(items.length !== 0) ? 'hidden' : ''}
              ${className}
            `}>
              알림이 없습니다.
            </div>
            <div className="text-[14px] text-background-500">            
              {
                (items.length !== 0) ? 
                <button>
                  모두 삭제 
                </button>
                : 
                <button onClick={setHidden}>
                  닫기 
                </button>
              }
            </div>
          </div>
          
        </div>

        <div className="overflow-y-auto">
          {items.map((item) => (
            <NotificationItem key={item.id} item={item}/>
          ))}
        </div>

    </div>
  );
}

export default NotificationBox;
