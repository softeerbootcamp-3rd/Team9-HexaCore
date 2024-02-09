import MainCar from '@/components/svgs/MainCar';
import Card from '@/pages/home/Card';
import type { Category } from '@/pages/home/homeRoutes';
import { useLoaderData } from 'react-router-dom';
import SearchBar from './Searchbar';
import SideBar from './Sidebar';

function Home() {
  const categoryData = useLoaderData() as Category[];
  return (
    <div>
      <div>
        <SearchBar />
      </div>
      <div className="flex pt-10 gap-4">
        <SideBar models={categoryData} />
        {/* <MainCar /> */}
        <div className="flex flex-wrap -mx-2">
          <Card />
          <Card />
          <Card />
          <Card />
        </div>
      </div>
    </div>
  );
}

export default Home;

