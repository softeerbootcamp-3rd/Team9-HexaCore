import React, { useState } from 'react';

type Props = {
  onTimeChange: (time: string) => void;
  id: string;
  className?: string;
};

function TimePicker({ onTimeChange, id, className }: Props) {
  const [selectedHour, setSelectedHour] = useState('12');

  const handleHourChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedHour(event.target.value);
    onTimeChange(event.target.value);
  };

  return (
    <select
      className={`
        text-center
        ${className}
      `}
      id={id}
      value={selectedHour}
      onChange={handleHourChange} >
      {
        Array.from({ length: 24 }, (_, i) => {
          const hour = (i + 1).toString().padStart(2, '0');
          return <option key={hour} value={hour}>{hour}</option>;
        })
      }
    </select >
  );
}

export default TimePicker;
