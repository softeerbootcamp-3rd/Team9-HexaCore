import Button from '@/components/Button';
import { useState } from 'react';
import Select, { SelectOption } from './Select';

type CarType = '경차' | '소형차' | '준중형차' | '중형차' | '대형차' | 'SUV' | '캠핑카' | 'VAN';

function SideBar() {
  const CarType = () => {
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

    const updateCarType = (carType: CarType) => {
      // 현재 carType의 활성 상태 가져오기
      const isActive = activeCarTypes.get(carType);
      // 새로운 Map을 생성하여 상태를 업데이트
      const updatedCarTypes = new Map(activeCarTypes).set(carType, !isActive);
      setActiveCarTypes(updatedCarTypes);
    };

    return (
      <div>
        {Array.from(activeCarTypes.keys()).map((carType) => (
          <button
            className={`rounded-xl p-4 m-1 text-sm
						${activeCarTypes.get(carType) ? 'bg-primary-100' : 'bg-background-100 text-background-400'}`}
            key={carType}
            onClick={() => updateCarType(carType)}>
            {carType}
          </button>
        ))}
      </div>
    );
  };

  const options = [
    { label: 'First', value: 1 },
    { label: 'Second', value: 2 },
    { label: 'Third', value: 3 },
    { label: 'Fourth', value: 4 },
    { label: 'Fifth', value: 5 },
  ];

  const SubCategorySelect = () => {
    const [model, setModel] = useState<SelectOption[]>([options[0]]);
    return (
      <div className="card center flex-col">
        <Select multiple options={options} value={model} onChange={(o) => setModel(o)} />
      </div>
    );
  };

  const CategorySelect = () => {
    const [model, setModel] = useState<SelectOption | undefined>(options[0]);
    return (
      <div className="card center flex-col">
        <Select options={options} value={model} onChange={(o) => setModel(o)} />
      </div>
    );
  };

  return (
    <div className="w-[300px] bg-white flex flex-col p-4">
      <div>
        <Button text="필터 적용하기" type="enabled" className="w-full h-12" />
      </div>
      <div className="p-4">
        <p className="pb-2">가격</p>
        <div className="flex pb-2 h-[45px] items-center">
          <div className="pr-2 text-sm">최저: </div>
          <input className="rounded-xl bg-background-100 h-full p-2"></input>
        </div>
        <div className="flex pb-2 h-[45px] items-center">
          <div className="pr-2 text-sm">최고: </div>
          <input className="rounded-xl bg-background-100 h-full p-2"></input>
        </div>
      </div>

      <div className="border-2 border-solid border-background-200"></div>

      <div className="p-4">
        <p className="pb-2">차종</p>
        <CarType />
      </div>

      <div className="border-2 border-solid border-background-200"></div>

      <div className="p-4">
        <p className="pb-2">모델</p>
        <CategorySelect />
        <p className="py-2">세부 모델</p>
        <SubCategorySelect />
      </div>
    </div>
  );
}

export default SideBar;

