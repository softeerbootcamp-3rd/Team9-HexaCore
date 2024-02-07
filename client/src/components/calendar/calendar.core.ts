export const DAYS = ['일', '월', '화', '수', '목', '금', '토'] as const;

export type DateRange = [Date, Date];

export const DATE_STATUS = {
  NONE: 'NONE',
  UNSELECTABLE: 'UNSELECTABLE',
  SELECTED: 'SELECTED',
  SELECTED_START: 'SELECTED_START',
  SELECTED_END: 'SELECTED_END',
  SELECTED_SINGLE: 'SELECTED_SINGLE',
  SELECTABLE: 'SELECTABLE',

  HOST_RESERVED: 'HOST_RESERVED',
  GUEST_SELECTABLE: 'GUEST_SELECTABLE',
};
export type DateStatus = (typeof DATE_STATUS)[keyof typeof DATE_STATUS];

export type DateInfo = {
  date: Date;
  status: DateStatus;
};

export const getLastDateOfThisMonth = (date: Date) => new Date(date.getFullYear(), date.getMonth() + 1, 0);

export const mergeDateRanges = (ranges: DateRange[]): DateRange[] => {
  const sortedRanges = ranges.sort((a, b) => a[0].getTime() - b[0].getTime());

  const mergedRanges: DateRange[] = [];
  let currentRange = sortedRanges[0];

  for (let i = 1; i < sortedRanges.length; i++) {
    const [currentStart, currentEnd] = currentRange;
    const [nextStart, nextEnd] = sortedRanges[i];

    if (nextStart.getTime() <= currentEnd.getTime() + 86400000) currentRange = [currentStart, new Date(Math.max(currentEnd.getTime(), nextEnd.getTime()))];
    else {
      mergedRanges.push(currentRange);
      currentRange = sortedRanges[i];
    }
  }

  mergedRanges.push(currentRange);

  return mergedRanges;
};

