import { NotificationData } from "@/fetches/notification/notification.type";
import CloseIcon from "../svgs/CloseIcon";

type Props = {
  item: NotificationData;
  onClick?: () => void;
  onlyOne?: boolean;
};

function NotificationItem({item, onClick, onlyOne=false}: Props) {
  return (
    <div className={`${(onlyOne) ? '' : 'border-t border-background-200'} flex flex-row justify-between px-5 py-[19px] w-[400px]`}>

      <div className='flex flex-col'>
        <div className='flex flex-row items-center'>
          <div className={
            `${item.title === '예약 완료' ? 'bg-primary-400' : 'bg-danger-400'} 
            rounded-full w-2 h-2 mr-3`}>
          </div>
          <h2 className='font-semibold text-background-600 text-[15px]'>
            {item.title}
          </h2>
        </div>

        <p className='mt-1 leading-6 text-background-500 text-[14px]'>
          {item.message}
        </p>
      </div>

      <div>
        <button onClick={onClick}>
          <CloseIcon />
        </button>
      </div>

    </div>
  );
}
  
export default NotificationItem;
  