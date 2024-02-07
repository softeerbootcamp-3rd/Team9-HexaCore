import { DATE_STATUS, DateInfo, DateRange, getLastDateOfThisMonth } from '../calendar.core';

type GuestCalendarState = {
  firstDate: Date;
  dateInfos: DateInfo[];
  selectedDateRange: DateRange;
  selectableDateRanges: DateRange[];
  isSelecting: boolean;
};

type GuestCalendarAction =
  | {
      type: 'NEXT_MONTH' | 'PREV_MONTH';
    }
  | {
      type: 'SELECT_START';
      payload: { selectedDateRange: DateRange };
    }
  | {
      type: 'SELECT_END';
      payload: { selectedDateRange: DateRange };
    }
  | {
      type: 'DESELECT';
      payload: { selectedDateRange: DateRange };
    }
  | {
      type: 'SET_SELECTABLE_DATE_RANGES';
      payload: { selectableDateRanges: DateRange[] };
    };

type GuestCalendarInit = {
  initDate: Date;
  selectedDateRange?: DateRange;
  selectableDateRanges: DateRange[];
};

export const guestCalendarInitializer = ({
  initDate,
  selectableDateRanges,
  selectedDateRange = [new Date(0), new Date(0)],
}: GuestCalendarInit): GuestCalendarState => {
  const firstDate = new Date(initDate.getFullYear(), initDate.getMonth(), 1);
  return {
    firstDate,
    dateInfos: generateDateInfos(firstDate, selectedDateRange, selectableDateRanges),
    selectableDateRanges,
    selectedDateRange,
    isSelecting: false,
  };
};

export const guestCalendarReducer = (state: GuestCalendarState, action: GuestCalendarAction): GuestCalendarState => {
  switch (action.type) {
    case 'NEXT_MONTH':
    case 'PREV_MONTH': {
      const firstDate = new Date(state.firstDate.getFullYear(), state.firstDate.getMonth() + (action.type === 'NEXT_MONTH' ? 1 : -1), 1);
      const dateInfos = generateDateInfos(firstDate, state.selectedDateRange, state.selectableDateRanges);
      return {
        ...state,
        firstDate,
        dateInfos,
      };
    }
    case 'SET_SELECTABLE_DATE_RANGES': {
      const { selectableDateRanges } = action.payload;
      const dateInfos = generateDateInfos(state.firstDate, state.selectedDateRange, selectableDateRanges);
      return {
        ...state,
        dateInfos,
        selectableDateRanges,
      };
    }
    case 'SELECT_START':
    case 'SELECT_END':
    case 'DESELECT': {
      const { selectedDateRange } = action.payload;
      const dateInfos = generateDateInfos(state.firstDate, selectedDateRange, state.selectableDateRanges);
      return {
        ...state,
        selectedDateRange,
        isSelecting: action.type === 'SELECT_START',
        dateInfos,
      };
    }
    default:
      return state;
  }
};

export const generateDateInfos = (firstDate: Date, selectedDateRange: DateRange, selectableDateRanges: DateRange[]): DateInfo[] => {
  const lastDate = getLastDateOfThisMonth(firstDate);
  const dateInfos: DateInfo[] = [];

  for (let i = 1; i <= firstDate.getDay(); i++) {
    dateInfos.push({ date: new Date(), status: DATE_STATUS.NONE });
  }

  const selectedStart = selectedDateRange[0].getTime();
  const selectedEnd = selectedDateRange[1].getTime();

  let selectableIndex = 0;
  for (let d = new Date(firstDate); d <= lastDate; d.setDate(d.getDate() + 1)) {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
    let status = DATE_STATUS.UNSELECTABLE;

    while (selectableIndex < selectableDateRanges.length) {
      const dateTime = date.getTime();
      const start = selectableDateRanges[selectableIndex][0].getTime();
      const end = selectableDateRanges[selectableIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime <= end) {
        if (dateTime === selectedStart) {
          status = dateTime === selectedEnd ? DATE_STATUS.SELECTED_SINGLE : DATE_STATUS.SELECTED_START;
          break;
        }
        if (dateTime === selectedEnd) {
          status = DATE_STATUS.SELECTED_END;
          break;
        }
        if (selectedStart < dateTime && dateTime < selectedEnd) {
          status = DATE_STATUS.SELECTED;
          break;
        }
        status = DATE_STATUS.GUEST_SELECTABLE;
        break;
      }
      selectableIndex++;
    }

    dateInfos.push({ date, status });
  }

  return dateInfos;
};

