type Props = {
  imgUrl: string;
  feePerHour: string;
  capacity: string;
  carName: string;
  address: string;
  fuel: string;
};

function Card({ imgUrl, feePerHour, capacity, carName, address, fuel }: Props) {
  return (
    <div className="w-full sm:w-1/3 px-2 mb-4">
      <div className="flex items-center overflow-hidden rounded-xl aspect-square bg-white">
        <img
          src="https://image-cdn.hypb.st/https%3A%2F%2Fkr.hypebeast.com%2Ffiles%2F2023%2F02%2Fhyundai-automobile-the-new-avante-revealed-01.jpg?cbr=1&q=90"
          className="object-cover"
        />
      </div>
      <div className="flex flex-col p-4">
        <div className="flex justify-between text-lg">
          <b>제네시스 N13</b>

          <div className="rounded-xl bg-primary-100 p-2 text-background-500">3인승</div>
        </div>
        <div>서울시 강남구 개포3동</div>
        <div>연비: 100km/l</div>
        <div className="ml-auto">
          <b>30,000원 / 시간</b>
        </div>
      </div>
    </div>
  );
}

export default Card;

