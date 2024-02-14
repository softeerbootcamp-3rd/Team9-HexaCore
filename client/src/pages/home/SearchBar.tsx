import Search from '@/components/svgs/Search';
import { CarData } from '@/pages/home/CarCard';
import { Dispatch, useEffect, useRef, useState } from 'react';
import response from '@/pages/home/dummy/cars.json';
import Map from '@/pages/home/Map';
import GuestCalendar from '@/components/calendar/guestCalendar/GuestCalendar';
import { DateRange } from '@/components/calendar/calendar.core';

type SearchBarProps = {
  setCarDataList: Dispatch<React.SetStateAction<CarData[]>>;
  setClickSearch: Dispatch<React.SetStateAction<boolean>>;
  rentDate: React.RefObject<HTMLInputElement>;
  returnDate: React.RefObject<HTMLInputElement>;
  people: React.RefObject<HTMLInputElement>;
  latitude: React.MutableRefObject<number>;
  longitude: React.MutableRefObject<number>;
};

function SearchBar({ setCarDataList, setClickSearch, /*rentDate, returnDate, */ people, latitude, longitude }: SearchBarProps) {
  const [address, setAddress] = useState<string>('차를 빌릴 위치');
  const [isOpenMap, setIsOpenMap] = useState<boolean>(false);
  const [rentDate, setRentDate] = useState<string>('빌릴 날짜');
  const [returnDate, setReturnDate] = useState<string>('반납할 날짜');
  const [isOpenCalendar, setIsOpenCalendar] = useState<boolean>(false);
  const calendarRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 드롭다운 외부 클릭 시 isOpenMap을 false로 설정
    function handleClickOutside(event: MouseEvent) {
      if (mapRef.current && !mapRef.current.contains(event.target as Node)) {
        setIsOpenMap(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [mapRef]);

  useEffect(() => {
    // 드롭다운 외부 클릭 시 isOpenCalendar을 false로 설정
    function handleClickOutside(event: MouseEvent) {
      if (calendarRef.current && !calendarRef.current.contains(event.target as Node)) {
        setIsOpenCalendar(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [calendarRef]);

  const handleSearch = async () => {
    const carDataList: CarData[] = response.data.cars;
    setCarDataList(carDataList);
    setClickSearch(true);
    console.log(
      'address > ',
      address,
      ' latitude > ',
      latitude.current,
      ' longitude > ',
      longitude.current,
      ' rentDate > ',
      rentDate,
      ' returnDate > ',
      returnDate,
      ' people > ',
      people.current?.value,
    );
  };

  const onReservationChange = (range: DateRange) => {
    console.log(range);
    if (range[0] == range[1]) {
      setRentDate(formatDate(range[0]));
      setReturnDate('');
    } else {
      setRentDate(formatDate(range[0]));
      setReturnDate(formatDate(range[1]));
    }
  };

  const formatDate = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    const formattedMonth = month < 10 ? `0${month}` : month.toString();
    const formattedDay = day < 10 ? `0${day}` : day.toString();

    return `${year}-${formattedMonth}-${formattedDay}`;
  };

  return (
    <div className="flex flex-col items-center">
      <p className="pb-6 pt-14 text-background-500">빌리고 싶은 차량을 검색해 보세요.</p>
      <div className="relative flex h-[68px] w-[743px] rounded-full border border-background-200 bg-white">
        <div className="flex w-1/4 rounded-full" onClick={() => setIsOpenMap((prev) => !prev)}>
          <label className="flex w-full flex-col justify-center px-6">
            <div>
              <b>위치</b>
            </div>
            <div className="text-sm text-background-400">{address}</div>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="flex w-1/4" onClick={() => setIsOpenCalendar((prev) => !prev)}>
          <label className="flex w-full flex-col justify-center px-6">
            <div>
              <b>대여일</b>
            </div>
            <div className="min-h-5 text-sm text-background-400">{rentDate}</div>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="flex w-1/4" onClick={() => setIsOpenCalendar((prev) => !prev)}>
          <label className="flex w-full flex-col justify-center px-6">
            <div>
              <b>반납일</b>
            </div>
            <div className="min-h-5 text-sm text-background-400">{returnDate}</div>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="flex w-1/4 rounded-full">
          <div className="flex w-[130px]">
            <label className="flex w-full flex-col justify-center pl-6">
              <div>
                <b>인원 수</b>
              </div>
              <input
                ref={people}
                onInput={(e) => {
                  e.currentTarget.value = e.currentTarget.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
                }}
                className="text-sm focus:outline-none"
                placeholder="탑승 인원"></input>
            </label>
          </div>
          <div className="flex w-1/4">
            <button onClick={handleSearch}>
              <Search />
            </button>
          </div>
        </div>
        {isOpenMap && (
          <div ref={mapRef} className="absolute top-[68px] z-10 rounded-xl bg-white p-4">
            <Map setAddress={setAddress} latitude={latitude} longitude={longitude} />{' '}
          </div>
        )}
        {isOpenCalendar && (
          <div ref={calendarRef} className="absolute left-1/4 top-[68px] z-10 w-1/2 rounded-xl bg-white p-4">
            <GuestCalendar onReservationChange={onReservationChange} />
          </div>
        )}
      </div>
    </div>
  );
}

export default SearchBar;

