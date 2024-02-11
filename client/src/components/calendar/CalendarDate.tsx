import { SELECT_STATUS, SelectStatus } from './calendar.core';

type Props = {
  size?: 'small' | 'large';
  date: Date;
  selectStatus: SelectStatus;
  onClick?: () => void;
};

function CalendarDate({ size = 'small', date, selectStatus, onClick }: Props) {
  switch (selectStatus) {
    case SELECT_STATUS.NONE:
      return <div className="flex aspect-square w-full items-center justify-center" />;

    case SELECT_STATUS.UNSELECTABLE:
      return <div className="flex aspect-square w-full items-center justify-center">{date.getDate()}</div>;

    case SELECT_STATUS.HOST_SELECTABLE:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div
            className={`flex cursor-pointer items-center justify-center rounded-full hover:bg-background-200 ${size === 'large' ? 'h-8 w-8' : 'h-6 w-6'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case SELECT_STATUS.GUEST_SELECTABLE:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div
            className={`flex cursor-pointer items-center justify-center rounded-full bg-background-200 hover:bg-primary-300 ${size === 'large' ? 'h-8 w-8' : 'h-6 w-6'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case SELECT_STATUS.SELECTED:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div className={`flex w-full items-center justify-center bg-primary-100 text-background-800 ${size === 'large' ? 'h-8' : 'h-6'}`}>
            {date.getDate()}
          </div>
        </div>
      );

    case SELECT_STATUS.SELECTED_SINGLE: {
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center rounded-full">
          <div
            className={`flex cursor-pointer items-center justify-center rounded-full bg-primary-300 hover:bg-primary-400 ${size === 'large' ? 'h-8 w-8' : 'h-6 w-6'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );
    }

    case SELECT_STATUS.SELECTED_START:
    case SELECT_STATUS.SELECTED_END:
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center rounded-full">
          <div
            className={`flex cursor-pointer items-center justify-center rounded-full bg-primary-300 
            before:absolute before:-z-10 before:w-1/2 before:bg-primary-100 hover:bg-primary-400
            ${size === 'large' ? 'h-8 w-8 before:h-8' : 'h-6 w-6 before:h-6'}
            ${selectStatus === SELECT_STATUS.SELECTED_START ? 'before:right-0' : 'before:left-0'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    default:
      return null;
  }
}

export default CalendarDate;

