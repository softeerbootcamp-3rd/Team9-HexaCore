import Search from '@/components/svgs/Search';

function Searchbar() {
  return (
    <div className="flex flex-col items-center">
      <p className="pb-4">빌리고 싶은 차량을 검색해 보세요.</p>
      <div className="h-[68px] w-[743px] flex rounded-full border bg-white">
        <div className="w-1/3 flex rounded-full">
          <label className="w-full px-8 flex flex-col justify-center">
            <div>위치</div>
            <input placeholder="차를 빌릴 위치"></input>
          </label>
        </div>
        <div className="border-r border-solid border-gray-400"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-8 flex flex-col justify-center">
            <div>대여일</div>
            <input placeholder="빌릴 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-gray-400"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-8 flex flex-col justify-center">
            <div>반납일</div>
            <input placeholder="반납할 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-gray-400"></div>
        <div className="rounded-full w-1/3 flex">
          <div className="w-3/4 flex">
            <label className="w-full pl-8 flex flex-col justify-center">
              <div>인원 수</div>
              <input placeholder="탑승 인원"></input>
            </label>
          </div>
          <div className="w-1/4 flex">
            <button>
              <Search />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Searchbar;

