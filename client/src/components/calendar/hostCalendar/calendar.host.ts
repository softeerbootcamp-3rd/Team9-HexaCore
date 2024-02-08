import { DateInfo, DateRange, SELECT_STATUS, SelectStatus, getLastDateOfThisMonth } from '../calendar.core';

type HostCalendarState = {
  firstDate: Date;
  dateInfos: DateInfo[];
  availableDates: DateRange[];
  reservations: DateRange[];
  isSelecting: boolean;
};

type HostCalendarAction =
  | {
      type: 'NEXT_MONTH' | 'PREV_MONTH';
    }
  | {
      type: 'SELECT_START';
      payload: { availableDates: DateRange[] };
    }
  | {
      type: 'SELECT_END';
      payload: { availableDates: DateRange[] };
    }
  | {
      type: 'DESELECT';
      payload: { availableDates: DateRange[] };
    }
  | {
      type: 'SET_RESERVATIONS';
      payload: { reservations: DateRange[] };
    };

type HostCalendarInit = {
  initDate: Date;
  availableDates: DateRange[];
  reservations: DateRange[];
};

export const hostCalendarInitializer = ({ initDate, reservations, availableDates }: HostCalendarInit): HostCalendarState => {
  const firstDate = new Date(initDate.getFullYear(), initDate.getMonth(), 1);
  return {
    firstDate,
    dateInfos: generateDateInfos({ firstDate, availableDates, reservations }),
    reservations,
    availableDates,
    isSelecting: false,
  };
};

export const hostCalendarReducer = (state: HostCalendarState, action: HostCalendarAction): HostCalendarState => {
  switch (action.type) {
    case 'NEXT_MONTH':
    case 'PREV_MONTH': {
      const firstDate = new Date(state.firstDate.getFullYear(), state.firstDate.getMonth() + (action.type === 'NEXT_MONTH' ? 1 : -1), 1);
      const dateInfos = generateDateInfos({ ...state, firstDate });
      return {
        ...state,
        firstDate,
        dateInfos,
      };
    }
    case 'SELECT_START': {
      if (state.isSelecting) return state;
      const { availableDates } = action.payload;
      return {
        ...state,
        availableDates,
        isSelecting: true,
      };
    }
    case 'SELECT_END': {
      if (!state.isSelecting) return state;
      const { availableDates } = action.payload;
      const dateInfos = generateDateInfos({ ...state, availableDates });
      return {
        ...state,
        availableDates,
        dateInfos,
        isSelecting: false,
      };
    }
    case 'DESELECT': {
      if (state.isSelecting) return state;
      const { availableDates } = action.payload;
      const dateInfos = generateDateInfos({ ...state, availableDates });
      return {
        ...state,
        availableDates,
        dateInfos,
        isSelecting: false,
      };
    }
    case 'SET_RESERVATIONS': {
      const { reservations } = action.payload;
      const dateInfos = generateDateInfos({ ...state, reservations });
      return {
        ...state,
        dateInfos,
        reservations,
      };
    }

    default:
      return state;
  }
};

export const generateDateInfos = ({ firstDate, availableDates }: { firstDate: Date; availableDates: DateRange[]; reservations: DateRange[] }): DateInfo[] => {
  const lastDate = getLastDateOfThisMonth(firstDate);
  const dateInfos: DateInfo[] = [];

  for (let i = 1; i <= firstDate.getDay(); i++) {
    dateInfos.push({ date: new Date(), status: SELECT_STATUS.NONE });
  }

  let selectedIndex = 0;
  for (let d = new Date(firstDate); d <= lastDate; d.setDate(d.getDate() + 1)) {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
    let status: SelectStatus = SELECT_STATUS.HOST_SELECTABLE;

    while (selectedIndex < availableDates.length) {
      const dateTime = date.getTime();
      const start = availableDates[selectedIndex][0].getTime();
      const end = availableDates[selectedIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime === start) {
        status = dateTime === end ? SELECT_STATUS.SELECTED_SINGLE : SELECT_STATUS.SELECTED_START;
        break;
      }
      if (dateTime < end) {
        status = SELECT_STATUS.SELECTED;
        break;
      }
      if (dateTime === end) {
        status = SELECT_STATUS.SELECTED_END;
        selectedIndex++;
        break;
      }

      selectedIndex++;
    }

    dateInfos.push({ date, status });
  }

  return dateInfos;
};

