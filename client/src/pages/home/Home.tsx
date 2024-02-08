import MainCar from '@/components/svgs/MainCar';
import SearchBar from './Searchbar';
import SideBar from './Sidebar';
import Card from './Card';

function Home() {
  return (
    <div>
      <div>
        <SearchBar />
      </div>
      <div className="flex pt-10 gap-4">
        <SideBar />
        {/* <MainCar /> */}
        <div className="flex flex-wrap -mx-2">
          <Card />
          <Card />
          <Card />
        </div>
      </div>
    </div>
  );
}

export default Home;

