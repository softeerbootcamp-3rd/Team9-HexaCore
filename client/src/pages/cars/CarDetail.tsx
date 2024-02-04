import { useLoaderData } from 'react-router-dom';

function CarDetail() {
  const carId = useLoaderData() as string;

  return (
    <div>
      <h2>/cars/:carId</h2>
      <p>carId: {carId}</p>
      <p> 차량 정보를 보고 예약이 가능한 페이지 입니다.</p>
    </div>
  );
}

export default CarDetail;

