import { useReducer } from 'react';
import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { DATE_STATUS, DAYS, DateRange, DateStatus, mergeDateRanges } from '../calendar.core';
import { hostCalendarInitializer, hostCalendarReducer } from './calendar.host';
import CalendarDay from '../CalendarDay';
import CalendarDate from '../CalendarDate';

type Props = {
  initDate?: Date;
  selectedDateRanges: DateRange[];
  reservedDateRanges: DateRange[];
  onSelectedDateRangesChange: (range: DateRange[]) => void;
};

function HostCalendar({ initDate = new Date(), selectedDateRanges, reservedDateRanges, onSelectedDateRangesChange }: Props) {
  const [state, calendarDispatch] = useReducer(hostCalendarReducer, { initDate, reservedDateRanges, selectedDateRanges }, hostCalendarInitializer);

  const handleDateSelect = (status: DateStatus, date: Date) => {
    let { selectedDateRanges } = state;
    switch (status) {
      case DATE_STATUS.SELECTED_START:
      case DATE_STATUS.SELECTED_END:
      case DATE_STATUS.SELECTED_SINGLE: {
        selectedDateRanges = selectedDateRanges.filter(([start, end]) => start.getTime() !== date.getTime() && end.getTime() !== date.getTime());
        calendarDispatch({ type: 'DESELECT', payload: { selectedDateRanges } });
        break;
      }
      case DATE_STATUS.HOST_SELECTABLE: {
        if (state.isSelecting) {
          const start = selectedDateRanges.at(-1)?.at(0);
          if (!start || start > date) return state;
          selectedDateRanges = mergeDateRanges([...selectedDateRanges.slice(0, -1), [start, date]]);
          calendarDispatch({ type: 'SELECT_END', payload: { selectedDateRanges } });
          break;
        }
        selectedDateRanges = [...selectedDateRanges, [date, date]];
        calendarDispatch({ type: 'SELECT_START', payload: { selectedDateRanges } });
        break;
      }
    }
    onSelectedDateRangesChange(selectedDateRanges);
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

export default HostCalendar;

/*
      const start = state.selectedDateRanges.at(-1)?.at(0);
      const end = action.payload.date;
      if (!start || start > end) return state;
      const selectedDateRanges = mergeDateRanges([...state.selectedDateRanges.slice(0, -1), [start, end]]);

      selectedDateRanges: [...state.selectedDateRanges, [start, start]],
*/

