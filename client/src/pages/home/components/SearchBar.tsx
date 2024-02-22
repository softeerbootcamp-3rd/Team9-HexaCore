import Search from '@/components/svgs/Search';
import { Dispatch, useEffect, useRef, useState } from 'react';
import Map from '@/pages/home/components/Map';
import GuestCalendar from '@/components/calendar/guestCalendar/GuestCalendar';
import { DateRange } from '@/components/calendar/calendar.core';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '@/utils/converters';

type SearchBarProps = {
  searchRange: DateRange;
  setSearchRange: React.Dispatch<React.SetStateAction<DateRange>>;
  party: string;
  setParty: Dispatch<React.SetStateAction<string>>;
  latitude: React.MutableRefObject<number>;
  longitude: React.MutableRefObject<number>;
  address: string;
  setAddress: Dispatch<React.SetStateAction<string>>;
};

function SearchBar({ searchRange, setSearchRange, party, setParty, latitude, longitude, address, setAddress }: SearchBarProps) {
  const [isOpenMap, setIsOpenMap] = useState<boolean>(false);
  const [isOpenCalendar, setIsOpenCalendar] = useState<boolean>(false);
  const calendarRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

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
    if (latitude.current !== 0 && longitude.current !== 0 && searchRange && party) {
      const queryString = new URLSearchParams({
        lat: latitude.current.toString(),
        lng: longitude.current.toString(),
        startDate: formatDate(searchRange[0]),
        endDate: formatDate(searchRange[1]),
        party: party,
        address: address,
      });
      navigate(`?${queryString}`);
    } else {
      alert('검색 조건을 모두 입력해주세요!');
    }
  };

  return (
    <div className='flex flex-col items-center'>
      <p className='pb-6 pt-14 text-background-500'>빌리고 싶은 차량을 검색해 보세요.</p>
      <div className='relative flex h-[68px] w-[743px] rounded-full border border-background-200 bg-white'>
        <div className='flex w-1/4 rounded-full' onClick={() => setIsOpenMap((prev) => !prev)}>
          <label className='flex w-full flex-col justify-center px-6'>
            <div>
              <b>위치</b>
            </div>
            <div className='text-sm text-background-400'>{address}</div>
          </label>
        </div>
        <div className='border-r border-solid border-background-200'></div>
        <div className='flex w-1/4' onClick={() => setIsOpenCalendar((prev) => !prev)}>
          <label className='flex w-full flex-col justify-center px-6'>
            <div>
              <b>대여일</b>
            </div>
            <div className='min-h-5 text-sm text-background-400'>
              {searchRange[0].toString() === new Date(0).toString() ? '빌릴 날짜' : formatDate(searchRange[0])}
            </div>
          </label>
        </div>
        <div className='border-r border-solid border-background-200'></div>
        <div className='flex w-1/4' onClick={() => setIsOpenCalendar((prev) => !prev)}>
          <label className='flex w-full flex-col justify-center px-6'>
            <div>
              <b>반납일</b>
            </div>
            <div className='min-h-5 text-sm text-background-400'>
              {searchRange[1].toString() === new Date(0).toString() ? '반납할 날짜' : formatDate(searchRange[1])}
            </div>
          </label>
        </div>
        <div className='border-r border-solid border-background-200'></div>
        <div className='flex w-1/4 rounded-full'>
          <div className='flex w-[130px]'>
            <label className='flex w-full flex-col justify-center pl-6'>
              <div>
                <b>인원 수</b>
              </div>
              <input
                value={party}
                onInput={(e) => {
                  e.currentTarget.value = e.currentTarget.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
                }}
                onChange={(e) => setParty(e.target.value)}
                className='text-sm focus:outline-none'
                placeholder='탑승 인원'></input>
            </label>
          </div>
          <div className='flex w-1/4'>
            <button onClick={handleSearch}>
              <Search />
            </button>
          </div>
        </div>
        {isOpenMap && (
          <div ref={mapRef} className='absolute top-[68px] z-10 rounded-xl bg-white p-4'>
            <Map setAddress={setAddress} latitude={latitude} longitude={longitude} />{' '}
          </div>
        )}
        {isOpenCalendar && (
          <div ref={calendarRef} className='absolute left-1/4 top-[68px] z-10 w-1/2 rounded-xl bg-white p-4'>
            <GuestCalendar onReservationChange={setSearchRange} reservation={searchRange} />
          </div>
        )}
      </div>
    </div>
  );
}

export default SearchBar;

