import CalendarDate from '../CalendarDate';
import { SELECT_STATUS, SelectStatus } from '../calendar.core';
import { RESERVATION_STATUS, ReservationStatus } from './calendar.host';

type Props = {
  date: Date;
  selectStatus: SelectStatus;
  reservationStatus: ReservationStatus;
  onClick?: () => void;
};

function HostCalendarDate({ date, selectStatus, reservationStatus, onClick }: Props) {
  if (reservationStatus === RESERVATION_STATUS.NONE) return <CalendarDate date={date} selectStatus={selectStatus} onClick={onClick} />;

  switch (reservationStatus) {
    default:
      return null;
  }
}

export default HostCalendarDate;

