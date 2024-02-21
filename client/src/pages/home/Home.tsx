import MainCar from '@/components/svgs/MainCar';
import CarCard from '@/pages/home/components/CarCard';
import { useLoaderData, useLocation } from 'react-router-dom';
import SearchBar from '@/pages/home/components/SearchBar';
import SideBar from '@/pages/home/components/sidebar/SideBar';
import { useEffect, useRef, useState } from 'react';
import { DateRange } from '@/components/calendar/calendar.core';
import { parseQueryString, type HomeLoaderResponse } from './homeRoutes';
import type { CarData, CarSearchParam } from '@/fetches/cars/cars.type';
import { fetchCars } from '@/fetches/cars/fetchCars';
import TopButton from '@/pages/home/components/TopButton';

function Home() {
  const location = useLocation();
  const loaderData = useLoaderData() as HomeLoaderResponse;
  const categoryData = loaderData.categories;
  const [carDataList, setCarDataList] = useState<CarData[]>([]);
  const [searchRange, setSearchRange] = useState<DateRange>([new Date(0), new Date(0)]);
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [party, setParty] = useState<string>('');
  const [address, setAddress] = useState<string>('차를 빌릴 위치');
  const latitude = useRef<number>(0);
  const longitude = useRef<number>(0);
  const [page, setPage] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(false);
  const loaderRef = useRef(null);

  useEffect(() => {
    // URL이 바뀌면 입력값 초기화
    setCarDataList(loaderData.cars?.data ? loaderData.cars.data : []);
    setPage(0);
    setHasNext(loaderData.cars?.pageInfo ? loaderData.cars.pageInfo.hasNext : false);

    const params = new URLSearchParams(location.search);

    const startDate = params.get('startDate');
    startDate ? setStartDate(startDate) : setStartDate('');

    const endDate = params.get('endDate');
    endDate ? setEndDate(endDate) : setEndDate('');
    startDate && endDate ? setSearchRange([new Date(startDate), new Date(endDate)]) : setSearchRange([new Date(0), new Date(0)]);

    const party = params.get('party');
    party ? setParty(party) : setParty('');

    const address = params.get('address');
    address ? setAddress(address) : setAddress('차를 빌릴 위치');
  }, [location.search]);

  // 무한 스크롤 요청
  useEffect(() => {
    const fetchData = async (params: CarSearchParam) => {
      const newCarData = await fetchCars({
        lat: params.lat,
        lng: params.lng,
        startDate: params.startDate,
        endDate: params.endDate,
        party: params.party,
        type: params.type,
        category: params.category,
        subcategory: params.subcategory,
        minPrice: params.minPrice,
        maxPrice: params.maxPrice,
        page: page,
      });

      if (newCarData?.data) {
        setCarDataList((prevCarDataList) => [...prevCarDataList, ...newCarData.data]);
      }
    };

    const carSearchParam = parseQueryString(window.location.href);
    if (carSearchParam && hasNext) {
      fetchData(carSearchParam);
    }
  }, [page]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          setPage((prevPage) => prevPage + 1);
        }
      },
      { threshold: 1.0, rootMargin: '0px 0px 30% 0px' },
    );

    if (loaderRef.current) {
      observer.observe(loaderRef.current);
    }

    return () => {
      if (loaderRef.current) {
        observer.unobserve(loaderRef.current);
      }
    };
  }, []);

  return (
    <div>
      <div>
        <SearchBar
          searchRange={searchRange}
          setSearchRange={setSearchRange}
          party={party}
          setParty={setParty}
          latitude={latitude}
          longitude={longitude}
          address={address}
          setAddress={setAddress}
        />
      </div>
      {!window.location.search ? (
        <div className='flex justify-center pt-20'>
          <MainCar />
        </div>
      ) : (
        <div className='flex gap-4 pt-10'>
          <SideBar models={categoryData} />
          <div className='-mx-2 flex w-full flex-wrap'>
            {carDataList.map((carData) => (
              <CarCard car={carData} startDate={startDate} endDate={endDate} />
            ))}
          </div>
        </div>
      )}
      <div ref={loaderRef}></div>
      <TopButton />
    </div>
  );
}

export default Home;

