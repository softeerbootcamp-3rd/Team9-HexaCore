import MainCar from '@/components/svgs/MainCar';
import CarCard from '@/pages/home/CarCard';
import { useLoaderData } from 'react-router-dom';
import SearchBar from '@/pages/home/SearchBar';
import SideBar from '@/pages/home/SideBar';
import { useEffect, useRef, useState } from 'react';
import { DateRange } from '@/components/calendar/calendar.core';
import { parseQueryString, type HomeLoaderResponse } from './homeRoutes';
import { formatDate } from '@/utils/converters';
import type { CarData, CarSearchParam } from '@/fetches/cars/cars.type';
import { fetchCars } from '@/fetches/cars/fetchCars';

function Home() {
  const loaderData = useLoaderData() as HomeLoaderResponse;
  const categoryData = loaderData.categories;
  const [carDataList, setCarDataList] = useState<CarData[]>([]);
  const [searchRange, setSearchRange] = useState<DateRange>([new Date(0), new Date(0)]);
  const party = useRef<HTMLInputElement>(null);
  const latitude = useRef<number>(0);
  const longitude = useRef<number>(0);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const loaderRef = useRef(null);

  useEffect(() => {
    setCarDataList(loaderData.cars ? loaderData.cars.data : []);
    setPage(0);
    setTotalPages(loaderData.cars ? loaderData.cars.pageInfo.totalPages : 0);
  }, [window.location.search]);

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
      setCarDataList((prevCarDataList) => [...prevCarDataList, ...newCarData.data]);
    };

    const carSearchParam = parseQueryString(window.location.href);
    if (carSearchParam && page <= totalPages) {
      fetchData(carSearchParam);
    }
  }, [page]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          console.log('isIntersecting ');
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
        <SearchBar searchRange={searchRange} setSearchRange={setSearchRange} people={party} latitude={latitude} longitude={longitude} />
      </div>
      {!window.location.search ? (
        <div className='flex justify-center pt-20'>
          <MainCar />
        </div>
      ) : (
        <div className='flex gap-4 pt-10'>
          <SideBar models={categoryData} latitude={latitude} longitude={longitude} searchRange={searchRange} people={party} />
          <div className='-mx-2 flex w-full flex-wrap'>
            {carDataList.map((carData) => (
              <CarCard car={carData} startDate={formatDate(searchRange[0])} endDate={formatDate(searchRange[1])} />
            ))}
          </div>
        </div>
      )}
      <div ref={loaderRef}></div>
    </div>
  );
}

export default Home;

