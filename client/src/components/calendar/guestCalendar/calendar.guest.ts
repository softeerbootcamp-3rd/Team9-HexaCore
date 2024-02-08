import { SELECT_STATUS, DateInfo, DateRange, getLastDateOfThisMonth, SelectStatus } from '../calendar.core';

type GuestCalendarState = {
  firstDate: Date;
  dateInfos: DateInfo[];
  reservation: DateRange;
  availableDates: DateRange[];
  isSelecting: boolean;
};

type GuestCalendarAction =
  | {
      type: 'NEXT_MONTH' | 'PREV_MONTH';
    }
  | {
      type: 'SELECT_START';
      payload: { reservation: DateRange };
    }
  | {
      type: 'SELECT_END';
      payload: { reservation: DateRange };
    }
  | {
      type: 'DESELECT';
      payload: { reservation: DateRange };
    }
  | {
      type: 'SET_AVAILABLE_DATES';
      payload: { availableDates: DateRange[] };
    };

type GuestCalendarInit = {
  initDate: Date;
  reservation?: DateRange;
  availableDates: DateRange[];
};

export const guestCalendarInitializer = ({ initDate, availableDates, reservation = [new Date(0), new Date(0)] }: GuestCalendarInit): GuestCalendarState => {
  const firstDate = new Date(initDate.getFullYear(), initDate.getMonth(), 1);
  return {
    firstDate,
    dateInfos: generateDateInfos({ availableDates, firstDate, reservation }),
    availableDates,
    reservation,
    isSelecting: false,
  };
};

export const guestCalendarReducer = (state: GuestCalendarState, action: GuestCalendarAction): GuestCalendarState => {
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
    case 'SET_AVAILABLE_DATES': {
      const { availableDates } = action.payload;
      const dateInfos = generateDateInfos({ ...state, availableDates });
      return {
        ...state,
        dateInfos,
        availableDates,
      };
    }
    case 'SELECT_START':
    case 'SELECT_END':
    case 'DESELECT': {
      const { reservation } = action.payload;
      const dateInfos = generateDateInfos({ ...state, reservation });
      return {
        ...state,
        reservation,
        isSelecting: action.type === 'SELECT_START',
        dateInfos,
      };
    }
    default:
      return state;
  }
};

export const generateDateInfos = ({
  firstDate,
  reservation,
  availableDates,
}: {
  firstDate: Date;
  reservation: DateRange;
  availableDates: DateRange[];
}): DateInfo[] => {
  const lastDate = getLastDateOfThisMonth(firstDate);
  const dateInfos: DateInfo[] = [];

  for (let i = 1; i <= firstDate.getDay(); i++) {
    dateInfos.push({ date: new Date(), status: SELECT_STATUS.NONE });
  }

  const selectedStart = reservation[0].getTime();
  const selectedEnd = reservation[1].getTime();

  let selectableIndex = 0;
  for (let d = new Date(firstDate); d <= lastDate; d.setDate(d.getDate() + 1)) {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
    let status: SelectStatus = SELECT_STATUS.UNSELECTABLE;

    while (selectableIndex < availableDates.length) {
      const dateTime = date.getTime();
      const start = availableDates[selectableIndex][0].getTime();
      const end = availableDates[selectableIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime <= end) {
        if (dateTime === selectedStart) {
          status = dateTime === selectedEnd ? SELECT_STATUS.SELECTED_SINGLE : SELECT_STATUS.SELECTED_START;
          break;
        }
        if (dateTime === selectedEnd) {
          status = SELECT_STATUS.SELECTED_END;
          break;
        }
        if (selectedStart < dateTime && dateTime < selectedEnd) {
          status = SELECT_STATUS.SELECTED;
          break;
        }
        status = SELECT_STATUS.GUEST_SELECTABLE;
        break;
      }
      selectableIndex++;
    }

    dateInfos.push({ date, status });
  }

  return dateInfos;
};

export const isValidReservation = (reservation: DateRange, availableDates: DateRange[]): boolean => {
  const [start, end] = reservation;
  return availableDates.some(([s, e]) => s.getTime() <= start.getTime() && end.getTime() <= e.getTime());
};

