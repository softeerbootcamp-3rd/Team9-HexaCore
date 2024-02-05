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
    <div>
      <h2>/hosts/manage</h2>

    </div>
  );
}


export default HostManage;