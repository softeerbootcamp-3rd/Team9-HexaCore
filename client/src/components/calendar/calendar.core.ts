export const DAYS = ['일', '월', '화', '수', '목', '금', '토'] as const;

export type DateRange = [Date, Date];

export const DATE_STATUS = {
  NONE: 'NONE',
  SELECTABLE: 'SELECTABLE',
  UNSELECTABLE: 'UNSELECTABLE',
  RESERVED: 'RESERVED',
  SELECTED: 'SELECTED',
  SELECTED_START: 'SELECTED_START',
  SELECTED_END: 'SELECTED_END',
  SELECTED_SINGLE: 'SELECTED_SINGLE',
};
export type DateStatus = (typeof DATE_STATUS)[keyof typeof DATE_STATUS];

export type DateInfo = {
  date: Date;
  status: DateStatus;
};

type HostCalendarState = {
  firstDate: Date;
  dateInfos: DateInfo[];
  selectedDateRanges: DateRange[];
  reservedDateRanges: DateRange[];
  isSelecting: boolean;
};

type HostCalendarAction = { type: unknown };

type CalendarInit = {
  initDate: Date;
  selectedDateRanges: DateRange[];
  reservedDateRanges: DateRange[];
};

export const calendarInitializer = ({ initDate, reservedDateRanges, selectedDateRanges }: CalendarInit): HostCalendarState => {
  const firstDate = new Date(initDate.getFullYear(), initDate.getMonth(), 1);
  return {
    firstDate,
    dateInfos: generateDateInfos(firstDate, selectedDateRanges),
    reservedDateRanges,
    selectedDateRanges,
    isSelecting: false,
  };
};

export const calendarReducer = (state: HostCalendarState, action: HostCalendarAction): HostCalendarState => {
  switch (action.type) {
    default:
      return state;
  }
};

const generateDateInfos = (firstDate: Date, selectedDateRanges: DateRange[]): DateInfo[] => {
  const lastDate = getLastDateOfThisMonth(firstDate);
  const dateInfos: DateInfo[] = [];

  for (let i = 1; i <= firstDate.getDay(); i++) {
    dateInfos.push({ date: new Date(), status: DATE_STATUS.NONE });
  }

  let selectedIndex = 0;

  for (let d = new Date(firstDate); d <= lastDate; d.setDate(d.getDate() + 1)) {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
    let status = DATE_STATUS.SELECTABLE;

    while (selectedIndex < selectedDateRanges.length) {
      if (date < selectedDateRanges[selectedIndex][0]) break;
      if (date.getTime() === selectedDateRanges[selectedIndex][0].getTime()) {
        status = date.getTime() === selectedDateRanges[selectedIndex][1].getTime() ? DATE_STATUS.SELECTED_SINGLE : DATE_STATUS.SELECTED_START;
        break;
      }
      if (date.getTime() < selectedDateRanges[selectedIndex][1].getTime()) {
        status = DATE_STATUS.SELECTED;
        break;
      }
      if (date.getTime() === selectedDateRanges[selectedIndex][1].getTime()) {
        status = DATE_STATUS.SELECTED_END;
        selectedIndex++;
        break;
      }
      selectedIndex++;
    }
    dateInfos.push({ date, status });
  }

  return dateInfos;
};

const getLastDateOfThisMonth = (date: Date) => new Date(date.getFullYear(), date.getMonth() + 1, 0);

