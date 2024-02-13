import Search from '@/components/svgs/Search';
import { CarData } from '@/pages/home/CarCard';
import { Dispatch, useState } from 'react';
import response from '@/pages/home/dummy/cars.json';
import Map from '@/pages/home/Map';

type SearchBarProps = {
  setCarDataList: Dispatch<React.SetStateAction<CarData[]>>;
  setClickSearch: Dispatch<React.SetStateAction<boolean>>;
  rentDate: React.RefObject<HTMLInputElement>;
  returnDate: React.RefObject<HTMLInputElement>;
  people: React.RefObject<HTMLInputElement>;
  latitude: React.MutableRefObject<number>;
  longitude: React.MutableRefObject<number>;
};

function SearchBar({ setCarDataList, setClickSearch, rentDate, returnDate, people, latitude, longitude }: SearchBarProps) {
  const [address, setAddress] = useState<string>('차를 빌릴 위치');
  const [isOpenMap, setIsOpenMap] = useState<boolean>(false);

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
      rentDate.current?.value,
      ' returnDate > ',
      returnDate.current?.value,
      ' people > ',
      people.current?.value,
    );
  };

  return (
    <div className="flex flex-col items-center">
      <p className="pt-14 pb-6 text-background-500">빌리고 싶은 차량을 검색해 보세요.</p>
      <div className="relative h-[68px] w-[743px] flex rounded-full border border-background-200 bg-white">
        <div className="w-1/3 flex rounded-full" onClick={() => setIsOpenMap((prev) => !prev)}>
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>위치</b>
            </div>
            <div className="text-sm text-background-400">{address}</div>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>대여일</b>
            </div>
            <input ref={rentDate} className="text-sm focus:outline-none" placeholder="빌릴 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>반납일</b>
            </div>
            <input ref={returnDate} className="text-sm focus:outline-none" placeholder="반납할 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="rounded-full w-1/3 flex">
          <div className="w-3/4 flex">
            <label className="w-full pl-6 flex flex-col justify-center">
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
          <div className="w-1/4 flex">
            <button onClick={handleSearch}>
              <Search />
            </button>
          </div>
        </div>
        {isOpenMap && <Map setAddress={setAddress} latitude={latitude} longitude={longitude} />}
      </div>
    </div>
  );
}

export default SearchBar;
