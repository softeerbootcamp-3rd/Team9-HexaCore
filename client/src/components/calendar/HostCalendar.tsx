import ChevronLeft from '@/components/svgs/ChevronLeft';
import ChevronRight from '@/components/svgs/ChevronRight';

function HostCalendar() {
  const today = new Date();

  return (
    <div className="flex flex-col gap-2 text-sm">
      <div className="flex items-center justify-between px-2">
        <button className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800">
          <ChevronLeft className="h-5 w-5" />
        </button>
        <span>{`${today.getFullYear()}년 ${today.getMonth() + 1}월`}</span>
        <button className="flex h-8 w-8 items-center justify-center rounded-full text-background-400 hover:bg-background-200 hover:text-background-800">
          <ChevronRight className="h-5 w-5" />
        </button>
      </div>

      <div className="grid grid-cols-7 grid-rows-7"></div>
    </div>
  );
}

export default HostCalendar;

