import Search from '@/components/svgs/Search';

function SearchBar() {
  return (
    <div className="flex flex-col items-center">
      <p className="pt-14 pb-6 text-background-500">빌리고 싶은 차량을 검색해 보세요.</p>
      <div className="h-[68px] w-[743px] flex rounded-full border border-background-200 bg-white">
        <div className="w-1/3 flex rounded-full">
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>위치</b>
            </div>
            <input className="text-sm focus:outline-none" placeholder="차를 빌릴 위치"></input>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>대여일</b>
            </div>
            <input className="text-sm focus:outline-none" placeholder="빌릴 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="w-1/6 flex">
          <label className="w-full px-6 flex flex-col justify-center">
            <div>
              <b>반납일</b>
            </div>
            <input className="text-sm focus:outline-none" placeholder="반납할 날짜"></input>
          </label>
        </div>
        <div className="border-r border-solid border-background-200"></div>
        <div className="rounded-full w-1/3 flex">
          <div className="w-3/4 flex">
            <label className="w-full pl-6 flex flex-col justify-center">
              <div>
                <b>인원 수</b>
              </div>
              <input className="text-sm focus:outline-none" placeholder="탑승 인원"></input>
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

export default SearchBar;

