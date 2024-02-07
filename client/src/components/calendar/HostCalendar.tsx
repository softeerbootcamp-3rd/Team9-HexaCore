import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';
import { DAYS, DateRange } from './calendar.core';
import CalendarDay from './CalendarDay';

type Props = {
  initDate?: Date;
  selectedDateRanges: DateRange[];
  onSelectedDateRangesChange: (range: DateRange[]) => void;
};

function HostCalendar({ initDate = new Date(), onSelectedDateRangesChange, selectedDateRanges }: Props) {
  return (
    <div className="flex flex-col gap-2 text-sm">
      <div className="flex items-center justify-between px-2">
        <button className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800">
          <ChevronLeft className="h-5 w-5" />
        </button>
        <span>{`${initDate.getFullYear()}년 ${initDate.getMonth() + 1}월`}</span>
        <button className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800">
          <ChevronRight className="h-5 w-5" />
        </button>
      </div>

      <div className="grid grid-cols-7 grid-rows-7">
        {DAYS.map((day, index) => (
          <CalendarDay key={index} day={day} />
        ))}
      </div>
    </div>
  );
}

export default HostCalendar;

