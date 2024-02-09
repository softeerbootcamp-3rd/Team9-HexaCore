import Button from '@/components/Button';
import { useRef, useState } from 'react';
import SingleSelect from '@/pages/home/SingleSelect';
import MultipleSelect from '@/pages/home/MultipleSelect';
import type { CarType } from '@/pages/home/CarType';
import SelectCarType from '@/pages/home/CarType';
import type { Category } from '@/pages/home/homeRoutes';

type SideBarProps = {
  models: Category[];
};

function SideBar({ models }: SideBarProps) {
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
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [selectedSubCategory, setSelectedSubCategory] = useState<string[]>([]);

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
    <div className="w-[300px] h-min-[800px] bg-white flex flex-col p-4 flex-shrink-0">
      <div>
        <Button text="필터 적용하기" type="enabled" className="w-full h-12" />
      </div>
      <div className="p-4">
        <p className="pb-2">최저 가격</p>
        <div className="flex min-h-[45px] cursor-default rounded-md bg-white py-1.5 px-3 text-left shadow-sm ring-1 ring-inset ring-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 sm:text-sm sm:leading-6">
          <input
            ref={minPrice}
            className="focus:outline-none"
            type="text"
            onKeyDown={validateNumber}
            onKeyUp={(e) => formatCurrency(e, minPrice)}
            onSelect={(e) => formatCurrency(e, minPrice)}
            maxLength={20}></input>
          <p className="flex items-center text-semibold text-background-400">{'원/ 시간'}</p>
        </div>

        {/* TODO: 최고 가격이 최저 가격보다 항상 높도록 설정 */}
        <p className="py-2">최고 가격</p>
        <div className="flex min-h-[45px] cursor-default rounded-md bg-white py-1.5 px-3 text-left shadow-sm ring-1 ring-inset ring-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 sm:text-sm sm:leading-6">
          <input
            ref={maxPrice}
            className="focus:outline-none"
            type="text"
            onKeyDown={validateNumber}
            onKeyUp={(e) => formatCurrency(e, maxPrice)}
            onSelect={(e) => formatCurrency(e, maxPrice)}
            maxLength={20}></input>
          <p className="flex items-center text-semibold text-background-400">{'원/ 시간'}</p>
        </div>
      </div>

      <div className="border border-solid border-background-200"></div>

      <div className="p-4">
        <p className="pb-2">차종</p>
        <SelectCarType activeCarTypes={activeCarTypes} setActiveCarTypes={setActiveCarTypes} />
      </div>

      <div className="border border-solid border-background-200"></div>

      <div className="p-4">
        <p className="pb-2">모델</p>
        <SingleSelect
          categoryList={Array.from(new Set(models.map((model) => model.category)))}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
          setSubSelectedCategory={setSelectedSubCategory}
        />
        <p className="py-2">세부 모델</p>
        <MultipleSelect
          categoryList={models.filter((model) => model.category === selectedCategory).map((model) => model.subCategory)}
          selectedSubCategory={selectedSubCategory}
          setSelectedSubCategory={setSelectedSubCategory}
        />
      </div>
    </div>
  );
}

export default SideBar;

