import { DateRange } from '@/components/calendar/calendar.core';

export const stringToDate = (date: string) => {
  const [year, month, day] = date.split('-').map((str) => parseInt(str, 10));
  return new Date(year, month - 1, day, 0, 0, 0, 0);
};

export const stringTupleToDateRange = (date: string[]) => {
  return date.map(stringToDate) as DateRange; // TODO: date.length !== 2, date[0] > date[1] validation
};

export const stringTuplesToDateRanges = (dates: string[][]) => {
  return dates
    .map(stringTupleToDateRange)
    .filter((date) => date.length === 2)
    .filter(([start, end]) => start <= end) as DateRange[];
};

