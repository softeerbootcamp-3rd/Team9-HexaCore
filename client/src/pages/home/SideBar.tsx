import Button from '@/components/Button';
import { useRef, useState } from 'react';
import SingleSelect from '@/pages/home/SingleSelect';
import MultipleSelect from '@/pages/home/MultipleSelect';
import type { CarType } from '@/pages/home/SelectCarType';
import SelectCarType from '@/pages/home/SelectCarType';
import type { Category, CategoryResponse } from '@/fetches/categories/categories.type';
import { formatDate } from '@/utils/converters';
import { DateRange } from '@/components/calendar/calendar.core';
import { useNavigate } from 'react-router-dom';

type SideBarProps = {
  models: CategoryResponse[];
  latitude: React.MutableRefObject<number>;
  longitude: React.MutableRefObject<number>;
  searchRange: DateRange;
  people: React.RefObject<HTMLInputElement>;
};

function SideBar({ models, latitude, longitude, searchRange, people }: SideBarProps) {
  const [activeCarTypes, setActiveCarTypes] = useState<Map<CarType, boolean>>(
    new Map([
      ['경차', false],
      ['소형차', false],
      ['준중형차', false],
      ['중형차', false],
      ['대형차', false],
      ['SUV', false],
      ['캠핑카', false],
      ['VAN', false],
    ]),
  );
  const minPrice = useRef<HTMLInputElement | null>(null);
  const maxPrice = useRef<HTMLInputElement | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<Category>();
  const [selectedSubcategory, setSelectedSubcategory] = useState<Category[]>([]);
  const navigate = useNavigate();

  const handleFilter = async () => {
    const minPriceNumber = Number(minPrice.current?.value.replace(/,/g, ''));
    const maxPriceNumber = Number(maxPrice.current?.value.replace(/,/g, ''));
    if (minPriceNumber !== 0 && maxPriceNumber !== 0 && minPriceNumber >= maxPriceNumber) {
      alert('최고 가격은 최저 가격보다 높아야 합니다.');
      return;
    }
    const activeKeys = Array.from(activeCarTypes.entries())
      .filter(([, value]) => value === true)
      .map(([key]) => key);

    if (latitude.current !== 0 && longitude.current !== 0 && searchRange && people.current?.value) {
      const queryString = new URLSearchParams({
        lat: latitude.current.toString(),
        lng: longitude.current.toString(),
        startDate: formatDate(searchRange[0]),
        endDate: formatDate(searchRange[1]),
        party: people.current?.value,
      });
      if (activeKeys) {
        queryString.append('type', activeKeys.toString());
      }
      if (selectedCategory) {
        queryString.append('category', selectedCategory.id.toString());
      }
      if (selectedSubcategory) {
        queryString.append('subcategory', selectedSubcategory.map((c) => c.id).toString());
      }
      if (minPriceNumber) {
        queryString.append('minPrice', minPriceNumber.toString());
      }
      if (maxPriceNumber) {
        queryString.append('maxPrice', maxPriceNumber.toString());
      }
      navigate(`?${queryString}`);
    }
  };

  // TODO: HostRegister.tsx에 있는 코드를 재활용할 수 있도록 수정
  // KeyboardEvent<HTMLInputElement> -> React.KeyboardEvent<HTMLInputElement> : "KeyboardEvent 형식이 제네릭이 아닙니다" 워닝 수정
  const validateNumber = (e: React.KeyboardEvent<HTMLInputElement>) => {
    const pattern = /^([0-9]|Backspace|ArrowLeft|ArrowRight)+$/; // 숫자, 백스페이스, 좌우 방향키를 허용하는 정규식
    const isValidInput = pattern.test(e.key);

    if (!isValidInput) {
      e.preventDefault();
    }
  };

  // e 타입 수정, ref 인자 추가
  const formatCurrency = (
    e: React.KeyboardEvent<HTMLInputElement> | React.SyntheticEvent<HTMLInputElement>,
    ref: React.MutableRefObject<HTMLInputElement | null>,
  ) => {
    if (e.currentTarget.value.length === 0 || !ref.current) return;

    const currencyValue = e.currentTarget.value ?? 0; // NaN일때 0으로 변환
    const currencyValueNumber = Number(currencyValue.replace(/,/g, ''));

    // 0 혹은 NaN이면 빈 칸으로 바꾼다.
    if (currencyValueNumber === 0 || Number.isNaN(currencyValueNumber)) {
      ref.current.value = '';
      return;
    }

    const currencyValueFilledZero = currencyValueNumber >= 1000 ? currencyValueNumber : currencyValueNumber * 1000;
    const formattedCurrency = currencyValueFilledZero.toLocaleString('ko-KR');
    // 0 3개마다 , 표시

    if (ref.current) {
      ref.current.value = formattedCurrency;
      // 입력 창의 커서를 ",000"의 앞의 위치로 강제한다.
      ref.current.selectionStart = e.currentTarget.value.length - 4;
      ref.current.selectionEnd = e.currentTarget.value.length - 4;
    }
  };

  return (
    <div className='h-min-[800px] flex w-[300px] flex-shrink-0 flex-col bg-white p-4'>
      <div>
        <Button text='필터 적용하기' type='enabled' className='h-12 w-full' onClick={handleFilter} />
      </div>
      <div className='p-4'>
        <p className='pb-2'>최저 가격</p>
        <div className='ring-gray-300 focus:ring-indigo-500 flex min-h-[45px] cursor-default rounded-md bg-white px-3 py-1.5 text-left shadow-sm ring-1 ring-inset focus:outline-none focus:ring-2 sm:text-sm sm:leading-6'>
          <input
            ref={minPrice}
            className='focus:outline-none'
            type='text'
            onKeyDown={validateNumber}
            onKeyUp={(e) => formatCurrency(e, minPrice)}
            onSelect={(e) => formatCurrency(e, minPrice)}
            maxLength={20}></input>
          <p className='text-semibold flex items-center text-background-400'>{'원 / 시간'}</p>
        </div>

        {/* TODO: 최고 가격이 최저 가격보다 항상 높도록 설정 */}
        <p className='py-2'>최고 가격</p>
        <div className='ring-gray-300 focus:ring-indigo-500 flex min-h-[45px] cursor-default rounded-md bg-white px-3 py-1.5 text-left shadow-sm ring-1 ring-inset focus:outline-none focus:ring-2 sm:text-sm sm:leading-6'>
          <input
            ref={maxPrice}
            className='focus:outline-none'
            type='text'
            onKeyDown={validateNumber}
            onKeyUp={(e) => formatCurrency(e, maxPrice)}
            onSelect={(e) => formatCurrency(e, maxPrice)}
            maxLength={20}></input>
          <p className='text-semibold flex items-center text-background-400'>{'원 / 시간'}</p>
        </div>
      </div>

      <div className='border border-solid border-background-200'></div>

      <div className='p-4'>
        <p className='pb-2'>차종</p>
        <SelectCarType activeCarTypes={activeCarTypes} setActiveCarTypes={setActiveCarTypes} />
      </div>

      <div className='border border-solid border-background-200'></div>

      <div className='p-4'>
        <p className='pb-2'>모델</p>
        <SingleSelect
          categoryList={models.map((model) => {
            return { id: model.id, name: model.name };
          })}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
          setSelectedSubcategory={setSelectedSubcategory}
        />
        <p className='py-2'>세부 모델</p>
        <MultipleSelect
          categoryList={models.find((model) => model.id === selectedCategory?.id)?.subcategories || []}
          selectedSubcategory={selectedSubcategory}
          setSelectedSubcategory={setSelectedSubcategory}
        />
      </div>
    </div>
  );
}

export default SideBar;

