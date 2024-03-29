import { useEffect, useReducer } from 'react';
import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { DAYS, DateRange, SELECT_STATUS, SelectStatus } from '../calendar.core';
import {
  RESERVATION_STATUS,
  ReservationStatus,
  closeSelectionFromAvailableDates,
  hostCalendarInitializer,
  hostCalendarReducer,
  openSelectionFromAvailableDates,
  removeDateFromAvailableDates,
} from './calendar.host';
import CalendarDay from '../CalendarDay';
import HostCalendarDate from './HostCalendarDate';

type Props = {
  size?: 'small' | 'large';
  initDate?: Date;
  availableDates: DateRange[];
  reservations: DateRange[];
  onAvailableDatesChange: (range: DateRange[]) => void;
};

function HostCalendar({ size = 'small', initDate = new Date(), availableDates, reservations, onAvailableDatesChange }: Props) {
  const [state, calendarDispatch] = useReducer(hostCalendarReducer, { initDate, reservations, availableDates }, hostCalendarInitializer);

  useEffect(() => {
    calendarDispatch({ type: 'SET_RESERVATIONS', payload: { reservations } });
  }, [reservations]);

  const handleDateSelect = ({ selectStatus, reservationStatus, date }: { selectStatus: SelectStatus; reservationStatus: ReservationStatus; date: Date }) => {
    if (reservationStatus !== RESERVATION_STATUS.NONE) return;

    let { availableDates } = state;
    switch (selectStatus) {
      case SELECT_STATUS.SELECTED_START:
      case SELECT_STATUS.SELECTED_END:
      case SELECT_STATUS.SELECTED_SINGLE: {
        availableDates = removeDateFromAvailableDates({ date, availableDates, reservations: state.reservations });
        calendarDispatch({ type: 'DESELECT', payload: { availableDates } });
        break;
      }
      case SELECT_STATUS.HOST_SELECTABLE: {
        if (state.isSelecting) {
          availableDates = closeSelectionFromAvailableDates({ date, availableDates });
          calendarDispatch({ type: 'SELECT_END', payload: { availableDates } });
          break;
        }
        availableDates = openSelectionFromAvailableDates({ date, availableDates });
        calendarDispatch({ type: 'SELECT_START', payload: { availableDates } });
        break;
      }
    }
    onAvailableDatesChange(availableDates);
  };

  return (
    <div className="flex select-none flex-col gap-2 text-sm">
      <div className="flex items-center justify-between px-2">
        <button
          className={`flex items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800 ${size === 'large' ? 'h-11 w-11' : 'h-8 w-8'}`}
          onClick={() => calendarDispatch({ type: 'PREV_MONTH' })}>
          <ChevronLeft className={size === 'large' ? 'h-8 w-8' : 'h-5 w-5'} />
        </button>
        <span className={size === 'large' ? 'text-lg font-semibold' : ''}>{`${state.firstDate.getFullYear()}년 ${state.firstDate.getMonth() + 1}월`}</span>
        <button
          className={`flex items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800 ${size === 'large' ? 'h-11 w-11' : 'h-8 w-8'}`}
          onClick={() => calendarDispatch({ type: 'NEXT_MONTH' })}>
          <ChevronRight className={size === 'large' ? 'h-8 w-8' : 'h-5 w-5'} />
        </button>
      </div>

      <div className={`grid grid-cols-7 grid-rows-7 ${size === 'large' ? 'text-lg' : 'text-sm'}`}>
        {DAYS.map((day, index) => (
          <CalendarDay key={index} day={day} />
        ))}
        {state.dateInfos.map(({ date, selectStatus, reservationStatus }, index) => (
          <HostCalendarDate
            size={size}
            key={index}
            date={date}
            selectStatus={selectStatus}
            reservationStatus={reservationStatus}
            onClick={() => handleDateSelect({ selectStatus, reservationStatus, date })}
          />
        ))}
      </div>
    </div>
  );
}

export default HostCalendar;

