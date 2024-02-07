import { useReducer } from 'react';
import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { DAYS, DateRange, calendarInitializer, calendarReducer } from './calendar.core';
import CalendarDay from './CalendarDay';
import CalendarDate from './CalendarDate';

type Props = {
  initDate?: Date;
  selectedDateRanges: DateRange[];
  reservedDateRanges: DateRange[];
  onSelectedDateRangesChange: (range: DateRange[]) => void;
};

function HostCalendar({ initDate = new Date(), selectedDateRanges, reservedDateRanges, onSelectedDateRangesChange }: Props) {
  const [state, calendarDispatch] = useReducer(calendarReducer, { initDate, reservedDateRanges, selectedDateRanges }, calendarInitializer);

  const handleDateSelect = (date: Date) => {};

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
          <CalendarDate key={index} date={date} status={status} onClick={() => handleDateSelect(date)} />
        ))}
      </div>
    </div>
  );
}

export default HostCalendar;

