import { DATE_STATUS, DateStatus } from './calendar.core';

type Props = {
  date: Date;
  status: DateStatus;
  onClick?: () => void;
};

function CalendarDate({ date, status, onClick }: Props) {
  switch (status) {
    case DATE_STATUS.NONE:
      return <div className="flex aspect-square w-full items-center justify-center" />;

    case DATE_STATUS.UNSELECTABLE:
      return <div className="flex aspect-square w-full items-center justify-center">{date.getDate()}</div>;

    case DATE_STATUS.HOST_SELECTABLE:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div className="flex h-6 w-6 cursor-pointer items-center justify-center rounded-full hover:bg-background-200" onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case DATE_STATUS.GUEST_SELECTABLE:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div className="flex h-6 w-6 cursor-pointer items-center justify-center rounded-full bg-background-200 hover:bg-primary-300" onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case DATE_STATUS.SELECTED:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div className="flex h-6 w-full items-center justify-center bg-primary-100 text-background-800">{date.getDate()}</div>
        </div>
      );
    case DATE_STATUS.SELECTED_SINGLE:
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center rounded-full">
          <div className="flex h-6 w-6 cursor-pointer items-center justify-center rounded-full bg-primary-300 hover:bg-primary-400" onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case DATE_STATUS.SELECTED_START:
    case DATE_STATUS.SELECTED_END:
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center rounded-full">
          <div
            className={`flex h-6 w-6 cursor-pointer items-center justify-center rounded-full bg-primary-300 before:absolute before:-z-10 before:h-6 before:w-1/2 before:bg-primary-100 hover:bg-primary-400
            ${status === DATE_STATUS.SELECTED_START ? 'before:right-0' : 'before:left-0'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );

    case DATE_STATUS.HOST_RESERVED:
      return null; // TODO:

    default:
      return null;
  }
}

export default CalendarDate;

