import { DAYS } from './calendar.core';

type Props = {
  day: (typeof DAYS)[number];
};

function CalendarDay({ day }: Props) {
  return <div className="flex aspect-square w-full items-center justify-center text-background-400">{day}</div>;
}

export default CalendarDay;

