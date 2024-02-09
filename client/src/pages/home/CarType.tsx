type CarType = '경차' | '소형차' | '준중형차' | '중형차' | '대형차' | 'SUV' | '캠핑카' | 'VAN';

type CarTypeProps = {
  activeCarTypes: Map<CarType, boolean>;
  setActiveCarTypes: React.Dispatch<React.SetStateAction<Map<CarType, boolean>>>;
};

function SelectCarType({ activeCarTypes, setActiveCarTypes }: CarTypeProps) {
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
          className={`rounded-xl p-3 m-1 text-sm 
            ${activeCarTypes.get(carType) ? 'bg-primary-100' : 'bg-background-100 text-background-400'}`}
          key={carType}
          onClick={() => updateCarType(carType)}>
          {carType}
        </button>
      ))}
    </div>
  );
}

export type { CarType };
export default SelectCarType;

