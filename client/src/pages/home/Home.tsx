import MainCar from '@/components/svgs/MainCar';
import CarCard from '@/pages/home/CarCard';
import type { Category } from '@/pages/home/homeRoutes';
import { useLoaderData } from 'react-router-dom';
import SearchBar from '@/pages/home/SearchBar';
import SideBar from '@/pages/home/SideBar';
import { useRef, useState } from 'react';
import type { CarData } from '@/pages/home/CarCard';

function Home() {
  const categoryData = useLoaderData() as Category[];
  const [carDataList, setCarDataList] = useState<CarData[]>([]);
  const [clickSearch, setClickSearch] = useState<boolean>(false);
  const rentDate = useRef<HTMLInputElement>(null);
  const returnDate = useRef<HTMLInputElement>(null);
  const people = useRef<HTMLInputElement>(null);
  const latitude = useRef<number>(0);
  const longitude = useRef<number>(0);

  return (
    <div>
      <div>
        <SearchBar
          setCarDataList={setCarDataList}
          setClickSearch={setClickSearch}
          rentDate={rentDate}
          returnDate={returnDate}
          people={people}
          latitude={latitude}
          longitude={longitude}
        />
      </div>
      {!clickSearch ? (
        <div className="flex justify-center pt-20">
          <MainCar />
        </div>
      ) : carDataList.length === 0 ? (
        <div className="flex gap-4 pt-10">
          <SideBar models={categoryData} latitude={latitude} longitude={longitude} rentDate={rentDate} returnDate={returnDate} people={people} />
          <div className="-mx-2 flex flex-wrap">
            <p className="text-background-400">검색 결과가 없습니다.</p>
          </div>
        </div>
      ) : (
        <div className="flex gap-4 pt-10">
          <SideBar models={categoryData} latitude={latitude} longitude={longitude} rentDate={rentDate} returnDate={returnDate} people={people} />
          <div className="-mx-2 flex w-full flex-wrap">
            {carDataList.map((carData) => (
              <CarCard
                id={carData.id}
                carName={carData.carName}
                imageUrl={carData.imageUrl}
                carAddress={carData.carAddress}
                mileage={carData.mileage}
                capacity={carData.capacity}
                feePerHour={carData.feePerHour}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;

