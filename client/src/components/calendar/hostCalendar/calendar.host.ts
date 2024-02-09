import { DateInfo, DateRange, SELECT_STATUS, SelectStatus, getLastDateOfThisMonth, mergeDateRanges } from '../calendar.core';

export const RESERVATION_STATUS = {
  NONE: 'NONE',
  RESERVED: 'RESERVED',
  RESERVED_START: 'RESERVED_START',
  RESERVED_END: 'RESERVED_END',
  RESERVED_SINGLE: 'RESERVED_SINGLE',
} as const;
export type ReservationStatus = (typeof RESERVATION_STATUS)[keyof typeof RESERVATION_STATUS];

type HostDateInfo = DateInfo & {
  reservationStatus: ReservationStatus;
};

type HostCalendarState = {
  firstDate: Date;
  dateInfos: HostDateInfo[];
  availableDates: DateRange[];
  reservations: DateRange[];
  isSelecting: boolean;
};

type HostCalendarAction =
  | { type: 'NONE' }
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
    dateInfos: generateHostDateInfos({ firstDate, availableDates, reservations }),
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
      const dateInfos = generateHostDateInfos({ ...state, firstDate });
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
      const dateInfos = generateHostDateInfos({ ...state, availableDates });
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
      const dateInfos = generateHostDateInfos({ ...state, availableDates });
      return {
        ...state,
        availableDates,
        dateInfos,
        isSelecting: false,
      };
    }
    case 'SET_RESERVATIONS': {
      const { reservations } = action.payload;
      const dateInfos = generateHostDateInfos({ ...state, reservations });
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

export const generateHostDateInfos = ({
  firstDate,
  reservations,
  availableDates,
}: {
  firstDate: Date;
  availableDates: DateRange[];
  reservations: DateRange[];
}): HostDateInfo[] => {
  const today = new Date();
  const lastDate = getLastDateOfThisMonth(firstDate);
  const dateInfos: HostDateInfo[] = [];

  for (let i = 1; i <= firstDate.getDay(); i++) {
    dateInfos.push({ date: today, selectStatus: SELECT_STATUS.NONE, reservationStatus: RESERVATION_STATUS.NONE });
  }

  let reservationIndex = 0;
  let selectedIndex = 0;

  for (let d = new Date(firstDate); d <= lastDate; d.setDate(d.getDate() + 1)) {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
    let selectStatus: SelectStatus =
      d < new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0, 0) ? SELECT_STATUS.UNSELECTABLE : SELECT_STATUS.HOST_SELECTABLE;
    let reservationStatus: ReservationStatus = RESERVATION_STATUS.NONE;

    while (reservationIndex < reservations.length) {
      const dateTime = date.getTime();
      const start = reservations[reservationIndex][0].getTime();
      const end = reservations[reservationIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime === start) {
        reservationStatus = dateTime === end ? RESERVATION_STATUS.RESERVED_SINGLE : RESERVATION_STATUS.RESERVED_START;
        break;
      }
      if (dateTime < end) {
        reservationStatus = RESERVATION_STATUS.RESERVED;
        break;
      }
      if (dateTime === end) {
        reservationStatus = RESERVATION_STATUS.RESERVED_END;
        reservationIndex++;
        break;
      }
      reservationIndex++;
    }

    while (selectedIndex < availableDates.length) {
      const dateTime = date.getTime();
      const start = availableDates[selectedIndex][0].getTime();
      const end = availableDates[selectedIndex][1].getTime();

      if (dateTime < start) break;
      if (dateTime === start) {
        selectStatus = dateTime === end ? SELECT_STATUS.SELECTED_SINGLE : SELECT_STATUS.SELECTED_START;
        break;
      }
      if (dateTime < end) {
        selectStatus = SELECT_STATUS.SELECTED;
        break;
      }
      if (dateTime === end) {
        selectStatus = SELECT_STATUS.SELECTED_END;
        selectedIndex++;
        break;
      }
      selectedIndex++;
    }

    dateInfos.push({ date, selectStatus, reservationStatus });
  }

  return dateInfos;
};

export const removeDateFromAvailableDates = ({
  date,
  availableDates,
  reservations,
}: {
  date: Date;
  availableDates: DateRange[];
  reservations: DateRange[];
}) => {
  const removedIdx = availableDates.findIndex(([start, end]) => start.getTime() === date.getTime() || end.getTime() === date.getTime());
  if (removedIdx === -1) return availableDates;

  const [removedStart, removedEnd] = availableDates[removedIdx];
  const removedReservation = reservations.find(([start, end]) => removedStart <= start && end <= removedEnd);
  if (!removedReservation) return [...availableDates.slice(0, removedIdx), ...availableDates.slice(removedIdx + 1)];

  return [...availableDates.slice(0, removedIdx), removedReservation, ...availableDates.slice(removedIdx + 1)];
};

export const closeSelectionFromAvailableDates = ({ date, availableDates }: { date: Date; availableDates: DateRange[] }) => {
  const start = availableDates.at(-1)?.at(0);
  if (!start || start > date) return availableDates;
  return mergeDateRanges([...availableDates.slice(0, -1), [start, date]]);
};

export const openSelectionFromAvailableDates = ({ date, availableDates }: { date: Date; availableDates: DateRange[] }): DateRange[] => [
  ...availableDates,
  [date, date],
];

