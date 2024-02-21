import { useEffect, useState } from 'react';

function TopButton() {
  const [isVisible, setIsVisible] = useState(false);

  // 스크롤 이벤트 리스너를 추가
  useEffect(() => {
    const toggleVisibility = () => {
      if (window.scrollY > 20) {
        setIsVisible(true);
      } else {
        setIsVisible(false);
      }
    };

    document.addEventListener('scroll', toggleVisibility);

    return () => window.removeEventListener('scroll', toggleVisibility);
  }, []);

  // 페이지 상단으로 스크롤하는 함수
  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  return (
    <div>
      {isVisible && (
        <button
          onClick={scrollToTop}
          className='fixed bottom-8 right-8 z-50 h-14 w-14 cursor-pointer rounded-full bg-primary-300 text-black shadow-lg hover:bg-primary-700 focus:outline-none'
          aria-label='Go to top'>
          <svg xmlns='http://www.w3.org/2000/svg' className='m-auto h-6 w-6' fill='none' viewBox='0 0 24 24' stroke='currentColor'>
            <path strokeLinecap='round' strokeLinejoin='round' strokeWidth={2} d='M5 15l7-7 7 7' />
          </svg>
        </button>
      )}
    </div>
  );
}

export default TopButton;

