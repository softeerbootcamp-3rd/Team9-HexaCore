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

type HostCalendarAction =
  | {
      type: 'NEXT_MONTH' | 'PREV_MONTH';
    }
  | {
      type: 'SELECT_START';
      payload: { date: Date };
    }
  | {
      type: 'SELECT_END';
      payload: { date: Date };
    };

type CalendarInit = {
  initDate: Date;
  selectedDateRanges: DateRange[];
  reservedDateRanges: DateRange[];
};

export const hostCalendarInitializer = ({ initDate, reservedDateRanges, selectedDateRanges }: CalendarInit): HostCalendarState => {
  const firstDate = new Date(initDate.getFullYear(), initDate.getMonth(), 1);
  return {
    firstDate,
    dateInfos: generateDateInfos(firstDate, selectedDateRanges),
    reservedDateRanges,
    selectedDateRanges,
    isSelecting: false,
  };
};

export const hostCalendarReducer = (state: HostCalendarState, action: HostCalendarAction): HostCalendarState => {
  switch (action.type) {
    case 'NEXT_MONTH':
    case 'PREV_MONTH': {
      const firstDate = new Date(state.firstDate.getFullYear(), state.firstDate.getMonth() + (action.type === 'NEXT_MONTH' ? 1 : -1), 1);
      const dateInfos = generateDateInfos(firstDate, state.selectedDateRanges);
      return {
        ...state,
        firstDate,
        dateInfos,
      };
    }
    case 'SELECT_START': {
      if (state.isSelecting) return state;
      const start = action.payload.date;
      return {
        ...state,
        selectedDateRanges: [...state.selectedDateRanges, [start, start]],
        isSelecting: true,
      };
    }
    case 'SELECT_END': {
      if (!state.isSelecting) return state;
      const start = state.selectedDateRanges.at(-1)?.at(0);
      const end = action.payload.date;
      if (!start || start > end) return state;
      const selectedDateRanges = [...state.selectedDateRanges];
      selectedDateRanges[selectedDateRanges.length - 1] = [start, end];
      selectedDateRanges.sort((a, b) => a[0].getTime() - b[0].getTime()); // TODO: merge
      const dateInfos = generateDateInfos(state.firstDate, selectedDateRanges);
      return {
        ...state,
        selectedDateRanges,
        dateInfos,
        isSelecting: false,
      };
    }
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
      const dateTime = date.getTime();
      const start = selectedDateRanges[selectedIndex][0].getTime();
      const end = selectedDateRanges[selectedIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime === start) {
        status = dateTime === end ? DATE_STATUS.SELECTED_SINGLE : DATE_STATUS.SELECTED_START;
        break;
      }
      if (dateTime < end) {
        status = DATE_STATUS.SELECTED;
        break;
      }
      if (dateTime === end) {
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

