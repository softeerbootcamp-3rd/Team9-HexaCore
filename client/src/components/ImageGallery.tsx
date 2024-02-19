import { useState } from 'react';

type Props = {
	imageUrls: string[];
	className?: string;
}

function ImageGallery({imageUrls, className} : Props) {

  const [currentIdx, setCurrentIdx] = useState(0);

	// 다음 이미지 표시
  const showNextImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx + 1) % imageUrls.length);
  };

  // 이전 이미지 표시
  const showPrevImage = () => {
    setCurrentIdx((prevIdx) => (prevIdx - 1 + imageUrls.length) % imageUrls.length);
  };
	
	return (
		<div className={`relative flex overflow-hidden ${className}`}>
			<button onClick={showPrevImage} className="absolute left-5 top-1/2 -translate-y-1/2 transform hover:brightness-75">
				<img src="/prev-button.svg" alt="Prev Button Image" />
			</button>
			<img className="w-full rounded-lg object-cover" src={imageUrls[currentIdx]} alt="car-img" />
			<button onClick={showNextImage} className="absolute right-5 top-1/2 -translate-y-1/2 transform hover:brightness-75">
				<img src="/next-button.svg" alt="Next Button Image" />
			</button>
		</div>
	);
}

export default ImageGallery;