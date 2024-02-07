import { DAYS } from './calendar.core';

type Props = {
  day: (typeof DAYS)[number];
};

function CalendarDay({ day }: Props) {
  return <div className="flex h-8 w-8 items-center justify-center text-xs text-background-400">{day}</div>;
}

export default CalendarDay;

