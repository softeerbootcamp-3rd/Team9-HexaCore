import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function HostManage() {
  const navigate = useNavigate();

  const isVehicleRegistered = true;

  useEffect(() => {
    if (!isVehicleRegistered) {
      navigate('/hosts/register');
    }
  }, [navigate, isVehicleRegistered]);

  return (
    <div className='flex flex-row min-h-screen'>
      <div className="w-1/2 p-6 pl-24">
        <h2 className='font-bold text-lg'>OO 호스트님, 등록한 차량을 관리해보세요.</h2>
        <div className='mt-6 h-2/3 rounded-3xl shadow-md	bg-white'></div>
      </div>
      <div className="w-1/2 p-6">
        <h2 className='font-bold text-lg'>예약 가능 일자</h2>
        <div className='mt-6 h-1/3 rounded-3xl shadow-md	bg-white'></div>
        <h2 className='mt-6 font-bold text-lg'>내 차 예약 내역</h2>
      </div>
    </div>

  );
}


export default HostManage;