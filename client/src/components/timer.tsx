import { useState, useEffect } from 'react';

function Timer() {
  const [seconds, setSeconds] = useState(0);
  const [isActive, setIsActive] = useState(false);

  // 타이머를 시작하거나 일시정지하는 함수
  function toggle() {
    setIsActive(!isActive);
  }

  // 타이머를 리셋하는 함수
  function reset() {
    setSeconds(0);
    setIsActive(false);
  }

  // isActive 상태 또는 seconds 상태가 변경될 때마다 호출되는 useEffect 훅
  useEffect(() => {
    if (isActive) {
      const interval = setInterval(() => {
        setSeconds((seconds) => seconds + 1);
      }, 1000);

      return () => clearInterval(interval);
    }
  }, [isActive]);
}

export default Timer;
