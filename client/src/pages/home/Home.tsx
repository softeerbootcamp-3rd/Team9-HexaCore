import MainCar from '@/components/svgs/MainCar';
import SearchBar from './Searchbar';
import SideBar from './Sidebar';

function Home() {
  return (
    <div>
      <div>
        <SearchBar />
      </div>
      <div className="flex pt-10">
        <SideBar />
        {/* <MainCar /> */}
      </div>
    </div>
  );
}

export default Home;

