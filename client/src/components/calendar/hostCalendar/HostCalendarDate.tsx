import CalendarDate from '../CalendarDate';
import { SELECT_STATUS, SelectStatus } from '../calendar.core';
import { RESERVATION_STATUS, ReservationStatus } from './calendar.host';

type Props = {
  size: 'small' | 'large';
  date: Date;
  selectStatus: SelectStatus;
  reservationStatus: ReservationStatus;
  onClick?: () => void;
};

function HostCalendarDate({ size, date, selectStatus, reservationStatus, onClick }: Props) {
  if (reservationStatus === RESERVATION_STATUS.NONE) return <CalendarDate size={size} date={date} selectStatus={selectStatus} onClick={onClick} />;

  switch (reservationStatus) {
    case RESERVATION_STATUS.RESERVED:
      return (
        <div className="flex aspect-square w-full items-center justify-center">
          <div className={`flex w-full items-center justify-center bg-danger-100 ${size === 'large' ? 'h-8' : 'h-6'}`}>{date.getDate()}</div>
        </div>
      );

    case RESERVATION_STATUS.RESERVED_START:
    case RESERVATION_STATUS.RESERVED_END: {
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center rounded-full">
          <div
            className={`flex items-center justify-center rounded-full bg-danger-100 
            before:absolute before:-z-10 before:w-1/2 before:bg-danger-100
            after:absolute after:-z-10 after:w-1/2
            ${size === 'large' ? 'h-8 w-8 before:h-8 after:h-8' : 'h-6 w-6 before:h-6 after:h-6'}
            ${
              selectStatus === SELECT_STATUS.SELECTED &&
              (reservationStatus === RESERVATION_STATUS.RESERVED_START ? 'after:left-0 after:bg-primary-100' : 'after:right-0 after:bg-primary-100')
            }
            ${reservationStatus === RESERVATION_STATUS.RESERVED_START ? 'before:right-0' : 'before:left-0'}`}
            onClick={onClick}>
            {date.getDate()}
          </div>
        </div>
      );
    }

    case RESERVATION_STATUS.RESERVED_SINGLE: {
      const isSelectedSingle = selectStatus === SELECT_STATUS.SELECTED_SINGLE;
      const isSelectedStart = selectStatus === SELECT_STATUS.SELECTED_START;
      const isSelectedEnd = selectStatus === SELECT_STATUS.SELECTED_END;
      return (
        <div className="relative z-0 flex aspect-square w-full items-center justify-center">
          <div
            className={`flex h-6 w-6 items-center justify-center rounded-full bg-danger-100 
          ${
            !isSelectedSingle &&
            (isSelectedStart
              ? `after:absolute after:right-0 after:-z-10 after:w-1/2 after:bg-primary-100 ${size === 'large' ? 'after:h-8' : 'after:h-6'}`
              : isSelectedEnd
                ? `after:absolute after:left-0 after:-z-10 after:w-1/2 after:bg-primary-100 ${size === 'large' ? 'after:h-8' : 'after:h-6'}`
                : `after:absolute after:-z-10 after:w-full after:bg-primary-100 ${size === 'large' ? 'after:h-8' : 'after:h-6'}`)
          }
          `}>
            {date.getDate()}
          </div>
        </div>
      );
    }
    default:
      return null;
  }
}

export default HostCalendarDate;

