import { useEffect, useReducer } from 'react';
import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { DATE_STATUS, DAYS, DateRange, DateStatus } from '../calendar.core';
import { guestCalendarInitializer, guestCalendarReducer, isValidReservation } from './calendar.guest';
import CalendarDay from '../CalendarDay';
import CalendarDate from '../CalendarDate';

type Props = {
  initDate?: Date;
  selectableDateRanges: DateRange[];
  selectedDateRange?: DateRange;
  onSelectedDateRangesChange: (range: DateRange) => void;
};

function GuestCalendar({ initDate = new Date(), selectableDateRanges, selectedDateRange, onSelectedDateRangesChange }: Props) {
  const [state, calendarDispatch] = useReducer(guestCalendarReducer, { initDate, selectableDateRanges, selectedDateRange }, guestCalendarInitializer);

  useEffect(() => {
    calendarDispatch({ type: 'SET_SELECTABLE_DATE_RANGES', payload: { selectableDateRanges } });
  }, [selectableDateRanges]);

  const handleDateSelect = (status: DateStatus, date: Date) => {
    let { selectedDateRange } = state;
    switch (status) {
      case DATE_STATUS.SELECTED_START:
      case DATE_STATUS.SELECTED_END:
      case DATE_STATUS.SELECTED_SINGLE: {
        selectedDateRange = [new Date(0), new Date(0)];
        calendarDispatch({ type: 'DESELECT', payload: { selectedDateRange } });
        break;
      }
      case DATE_STATUS.GUEST_SELECTABLE: {
        if (state.isSelecting) {
          const start = selectedDateRange.at(0);
          if (!start || start > date) return;
          selectedDateRange = [start, date];
          if (!isValidReservation(selectedDateRange, state.selectableDateRanges)) return;
          calendarDispatch({ type: 'SELECT_END', payload: { selectedDateRange } });
          break;
        }
        selectedDateRange = [date, date];
        calendarDispatch({ type: 'SELECT_START', payload: { selectedDateRange } });
        break;
      }
    }
    onSelectedDateRangesChange(selectedDateRange);
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

      <div className="grid grid-cols-7 grid-rows-7">
        {DAYS.map((day, index) => (
          <CalendarDay key={index} day={day} />
        ))}
        {state.dateInfos.map(({ date, status }, index) => (
          <CalendarDate key={index} date={date} status={status} onClick={() => handleDateSelect(status, date)} />
        ))}
      </div>
    </div>
  );
}

export default GuestCalendar;

