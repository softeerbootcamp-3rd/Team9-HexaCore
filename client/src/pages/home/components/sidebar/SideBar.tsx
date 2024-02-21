import Button from '@/components/Button';
import { useEffect, useState } from 'react';
import SingleSelect from '@/pages/home/components/sidebar/SingleSelect';
import MultipleSelect from '@/pages/home/components/sidebar/MultipleSelect';
import type { CarType } from '@/pages/home/components/sidebar/SelectCarType';
import SelectCarType from '@/pages/home/components/sidebar/SelectCarType';
import type { Category, CategoryResponse } from '@/fetches/categories/categories.type';
import { useLocation, useNavigate } from 'react-router-dom';
import { useCustomToast } from '@/components/Toast';

type SideBarProps = {
  models: CategoryResponse[];
};

function SideBar({ models }: SideBarProps) {
  const defaultCarTypeState: Map<CarType, boolean> = new Map([
    ['경차', false],
    ['소형차', false],
    ['준중형차', false],
    ['중형차', false],
    ['대형차', false],
    ['SUV', false],
    ['캠핑카', false],
    ['VAN', false],
  ]);
  const [activeCarTypes, setActiveCarTypes] = useState<Map<CarType, boolean>>(defaultCarTypeState);
  const [minPrice, setMinPrice] = useState<string>('');
  const [maxPrice, setMaxPrice] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedSubcategory, setSelectedSubcategory] = useState<Category[]>([]);
  const navigate = useNavigate();
  const location = useLocation();
  const { ToastComponent, showToast } = useCustomToast();

  useEffect(() => {
    // URL이 바뀌면 필터링 조건 초기화
    setSelectedCategory(null);
    setSelectedSubcategory([]);
    setActiveCarTypes(defaultCarTypeState);
    setMinPrice('');
    setMaxPrice('');
  }, [location.search]);

  const handleFilter = async () => {
    const minPriceNumber = Number(minPrice.replace(/,/g, ''));
    const maxPriceNumber = Number(maxPrice.replace(/,/g, ''));
    if (minPriceNumber !== 0 && maxPriceNumber !== 0 && minPriceNumber >= maxPriceNumber) {
      showToast('검색 실패', '최고 가격은 최저 가격보다 높아야합니다.');
      return;
    }
    
    const activeKeys = Array.from(activeCarTypes.entries())
      .filter(([, value]) => value === true)
      .map(([key]) => key);

    const queryString = new URLSearchParams(location.search);
    if (activeKeys.length !== 0) {
      queryString.append('type', activeKeys.toString());
    }
    if (selectedCategory) {
      queryString.append('category', selectedCategory.id.toString());
    }
    if (selectedSubcategory.length !== 0) {
      queryString.append('subcategory', selectedSubcategory.map((c) => c.id).toString());
    }
    if (minPriceNumber) {
      queryString.append('minPrice', minPriceNumber.toString());
    }
    if (maxPriceNumber) {
      queryString.append('maxPrice', maxPriceNumber.toString());
    }
    navigate(`?${queryString}`);
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

  const formatCurrency = (value: string) => {
    if (!value) return '';

    // 입력 값에서 콤마를 제거하고 숫자로 변환
    const numberValue = Number(value.replace(/,/g, ''));

    // 0 혹은 NaN이면 빈 문자열 반환
    if (numberValue === 0 || isNaN(numberValue)) {
      return '';
    }

    // 숫자를 통화 형식으로 포맷팅
    return numberValue.toLocaleString('ko-KR');
  };

  // minPrice와 maxPrice 입력 필드의 onChange 이벤트 핸들러
  const handleMinPriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // 입력 값 포맷팅 후 상태 업데이트
    const formattedValue = formatCurrency(e.target.value);
    setMinPrice(formattedValue);
  };

  const handleMaxPriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // 입력 값 포맷팅 후 상태 업데이트
    const formattedValue = formatCurrency(e.target.value);
    setMaxPrice(formattedValue);
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
            value={minPrice}
            className='focus:outline-none'
            type='text'
            onKeyDown={validateNumber}
            onChange={(e) => handleMinPriceChange(e)}
            maxLength={20}></input>
          <p className='text-semibold flex items-center text-background-400'>{'원 / 시간'}</p>
        </div>

        {/* TODO: 최고 가격이 최저 가격보다 항상 높도록 설정 */}
        <p className='py-2'>최고 가격</p>
        <div className='ring-gray-300 focus:ring-indigo-500 flex min-h-[45px] cursor-default rounded-md bg-white px-3 py-1.5 text-left shadow-sm ring-1 ring-inset focus:outline-none focus:ring-2 sm:text-sm sm:leading-6'>
          <input
            value={maxPrice}
            className='focus:outline-none'
            type='text'
            onKeyDown={validateNumber}
            onChange={(e) => handleMaxPriceChange(e)}
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
      <ToastComponent />
    </div>
  );
}

export default SideBar;

