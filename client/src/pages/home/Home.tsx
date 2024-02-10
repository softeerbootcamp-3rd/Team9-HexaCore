import MainCar from '@/components/svgs/MainCar';
import CarCard from '@/pages/home/CarCard';
import type { Category } from '@/pages/home/homeRoutes';
import { useLoaderData } from 'react-router-dom';
import SearchBar from '@/pages/home/Searchbar';
import SideBar from '@/pages/home/Sidebar';
import { useRef, useState } from 'react';
import type { CarData } from '@/pages/home/CarCard';

function Home() {
  const categoryData = useLoaderData() as Category[];
  const [carDataList, setCarDataList] = useState<CarData[]>([]);
  const [clickSearch, setClickSearch] = useState<boolean>(false);
  const address = useRef<HTMLInputElement>(null);
  const rentDate = useRef<HTMLInputElement>(null);
  const returnDate = useRef<HTMLInputElement>(null);
  const people = useRef<HTMLInputElement>(null);

  return (
    <div>
      <div>
        <SearchBar
          setCarDataList={setCarDataList}
          setClickSearch={setClickSearch}
          address={address}
          rentDate={rentDate}
          returnDate={returnDate}
          people={people}
        />
      </div>
      {!clickSearch ? (
        <div className="flex justify-center pt-20">
          <MainCar />
        </div>
      ) : carDataList.length === 0 ? (
        <div className="flex pt-10 gap-4">
          <SideBar models={categoryData} address={address} rentDate={rentDate} returnDate={returnDate} people={people} />
          <div className="flex flex-wrap -mx-2">
            <p className="text-background-400">검색 결과가 없습니다.</p>
          </div>
        </div>
      ) : (
        <div className="flex pt-10 gap-4">
          <SideBar models={categoryData} address={address} rentDate={rentDate} returnDate={returnDate} people={people} />
          <div className="flex flex-wrap -mx-2 w-full">
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

