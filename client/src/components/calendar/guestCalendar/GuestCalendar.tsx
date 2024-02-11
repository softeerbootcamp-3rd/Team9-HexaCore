import { useEffect, useReducer } from 'react';
import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { SELECT_STATUS, DAYS, DateRange, SelectStatus } from '../calendar.core';
import { guestCalendarInitializer, guestCalendarReducer, isValidReservation } from './calendar.guest';
import CalendarDay from '../CalendarDay';
import CalendarDate from '../CalendarDate';

type Props = {
  initDate?: Date;
  availableDates: DateRange[];
  reservation?: DateRange;
  onReservationChange: (range: DateRange) => void;
};

function GuestCalendar({ initDate = new Date(), availableDates, reservation, onReservationChange }: Props) {
  const [state, calendarDispatch] = useReducer(guestCalendarReducer, { initDate, availableDates, reservation }, guestCalendarInitializer);

  useEffect(() => {
    calendarDispatch({ type: 'SET_AVAILABLE_DATES', payload: { availableDates } });
  }, [availableDates]);

  const handleDateSelect = (selectStatus: SelectStatus, date: Date) => {
    let { reservation } = state;
    switch (selectStatus) {
      case SELECT_STATUS.SELECTED_START:
      case SELECT_STATUS.SELECTED_END:
      case SELECT_STATUS.SELECTED_SINGLE: {
        reservation = [new Date(0), new Date(0)];
        calendarDispatch({ type: 'DESELECT', payload: { reservation } });
        break;
      }
      case SELECT_STATUS.GUEST_SELECTABLE: {
        if (state.isSelecting) {
          const start = reservation.at(0);
          if (!start || start > date) return;
          reservation = [start, date];
          if (!isValidReservation(reservation, state.availableDates)) return;
          calendarDispatch({ type: 'SELECT_END', payload: { reservation } });
          break;
        }
        reservation = [date, date];
        calendarDispatch({ type: 'SELECT_START', payload: { reservation } });
        break;
      }
    }
    onReservationChange(reservation);
  };

  return (
    <div className="flex select-none flex-col gap-2 text-sm">
      <div className="flex items-center justify-between px-2">
        <button
          className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800"
          onClick={() => calendarDispatch({ type: 'PREV_MONTH' })}>
          <ChevronLeft className="h-5 w-5" />
        </button>
        <span>{`${state.firstDate.getFullYear()}년 ${state.firstDate.getMonth() + 1}월`}</span>
        <button
          className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800"
          onClick={() => calendarDispatch({ type: 'NEXT_MONTH' })}>
          <ChevronRight className="h-5 w-5" />
        </button>
      </div>

      <div className="grid grid-cols-7 grid-rows-7 text-sm">
        {DAYS.map((day, index) => (
          <CalendarDay key={index} day={day} />
        ))}
        {state.dateInfos.map(({ date, selectStatus }, index) => (
          <CalendarDate key={index} date={date} selectStatus={selectStatus} onClick={() => handleDateSelect(selectStatus, date)} />
        ))}
      </div>
    </div>
  );
}

export default GuestCalendar;

