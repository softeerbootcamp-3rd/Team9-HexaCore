import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import dummyData from './CarData.json';
import Tag from '@/components/Tag';
import Button from '@/components/Button';

const initialDataState = {
  success: false,
  code: 0,
  message: '',
  data: {
    car: {
      carName: '',
      carNumber: '',
      imageUrls: [],
      mileage: 0,
      fuel: '',
      type: '',
      capacity: 0,
      year: 0,
      feePerHour: 0,
      carAddress: '',
      description: '',
      dates: []
    },
  },
};

function HostManage() {
  const navigate = useNavigate();
  const [data, setData] = useState(initialDataState);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const handleNextImage = (index) => {

    setCurrentImageIndex(index);
  };

  const editCar = () => {
    alert("수정이 완료되었습니다.");
    location.reload();
  };

  const deleteCar = () => {
    alert("삭제가 완료되었습니다.");
    location.reload();
  };

  const isVehicleRegistered = true;

  useEffect(() => {
    if (!isVehicleRegistered) {
      navigate('/hosts/register');
    }
    else {
      setData(dummyData);
    }
  }, [navigate, isVehicleRegistered]);

  return (
    <div className='flex flex-row min-h-full'>
      <div className='w-1/2 p-6 pl-18 pt-0 flex flex-col mb-3'>
        <h2 className='font-bold text-lg'>OO 호스트님, 등록한 차량을 관리해보세요.</h2>
        <div className='w-full mt-6 flex-grow p-6 rounded-3xl shadow-md	bg-white'>
          <div className='w-full h-1/3 border-b border-b-gray-300 flex'>
            <div className='w-40 h-full'>
              <div className='h-3/4 w-full'>
                <img className='w-full h-full rounded-lg' src={data.data.car.imageUrls[currentImageIndex]}></img>

              </div>
              <div className='h-1/5 flex justify-center items-center'>
                {data.data.car.imageUrls.map((url, index) => (
                  <div
                    key={index}
                    className='cursor-pointer w-4 h-4 mx-1 bg-background-300 rounded-full'
                    onClick={() => handleNextImage(index)}
                  />
                ))}
              </div>
            </div>
            <div className='flex-grow h-full pl-5 flex flex-col'>
              <div className='h-1/2'>
                <h2 className='font-bold text-md'>모델 : {data.data.car.carName}</h2>
                <h2 className='font-bold text-md'>차량번호 : {data.data.car.carNumber}</h2>
              </div>

              <div className='h-1/2 flex flex-wrap'>
                {data.data.car.type && <Tag text={data.data.car.type}></Tag>}
                {data.data.car.capacity && <Tag text={`${data.data.car.capacity}인승`}></Tag>}
                {data.data.car.fuel && <Tag text={data.data.car.fuel}></Tag>}
                {data.data.car.mileage && <Tag text={`${data.data.car.mileage}km/L`}></Tag>}
              </div>
            </div>
          </div>
          <div className='w-full h-1/2 flex flex-col'>
            <h2 className='font-bold text-md mt-4'>위치 : {data.data.car.carAddress}</h2>
            <div className='w-full h-4/5 mt-4 flex'>
              <h2 className='font-bold text-md min-w-fit'>부가설명 : </h2>
              <div className='w-4/5 bg-background-100 ml-4 rounded-lg p-6 text-sm break-words overflow-hidden'>
                <div style={{ display: '-webkit-box', WebkitBoxOrient: 'vertical', overflow: 'hidden', WebkitLineClamp: 6 }}>
                  {data.data.car.description}
                </div>
              </div>
            </div>
          </div>
          <div className='w-full h-14 flex flex-col mt-6'>
            <h2 className='font-bold text-md text-right mb-3'>{data.data.car.feePerHour}원/ 일</h2>
            <div className='flex flex-row-reverse'>
              <Button className='ml-6' text='삭제' type='danger' onClick={deleteCar}></Button>
              <Button className='ml-6' text='수정' onClick={editCar}></Button>

            </div>
          </div>
        </div>

      </div>
      <div className='w-1/2 p-6 pt-0'>
        <h2 className='font-bold text-lg'>예약 가능 일자</h2>
        <div className='mt-6 h-1/3 rounded-3xl shadow-md	bg-white'></div>
        <h2 className='mt-6 font-bold text-lg'>내 차 예약 내역</h2>
      </div>
    </div>

  );
}


export default HostManage;