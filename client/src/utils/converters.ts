import { DateRange } from '@/components/calendar/calendar.core';

export const stringToDate = (date: string) => {
  const [year, month, day] = date.split('-').map((str) => parseInt(str, 10));
  return new Date(year, month - 1, day, 0, 0, 0, 0);
};

const stringToDateTime = (date: string) => {
  return new Date(date);
};

export const stringTupleToDateRange = (date: string[]) => {
  return date.map(stringToDate) as DateRange; // TODO: date.length !== 2, date[0] > date[1] validation
};

export const stringTupleToDateTimeRange = (date: string[]) => {
  return date.map(stringToDateTime) as DateRange; // TODO: date.length !== 2, date[0] > date[1] validation
};

export const stringTuplesToDateRanges = (dates: string[][]) => {
  return dates
    .map(stringTupleToDateRange)
    .filter((date) => date.length === 2)
    .filter(([start, end]) => start <= end) as DateRange[];
};

export const formatDate = (date: Date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();

  const formattedMonth = month < 10 ? `0${month}` : month.toString();
  const formattedDay = day < 10 ? `0${day}` : day.toString();

  return `${year}-${formattedMonth}-${formattedDay}`;
};

// DateTime을 string으로 변환
export const dateTimeToString = (dateTime: Date) => {
  const date = formatDate(dateTime);
  const hours = dateTime.getHours().toString().padStart(2, '0');
  const time = `${hours}:00:00`
  return `${date}T${time}`;
};

export const dateTimeFormatter = (dateTime: Date) => {
  const date = formatDate(dateTime);
  const hours = dateTime.getHours().toString().padStart(2, '0');
  return `${date} ${hours}시`
}

const dateRangeToString = (dateRange: DateRange) => {
  return dateRange.map(dateTimeToString)
}

export const dateRangesToString = (dateRanges: DateRange[]) => {
  return dateRanges.map(dateRangeToString)
}