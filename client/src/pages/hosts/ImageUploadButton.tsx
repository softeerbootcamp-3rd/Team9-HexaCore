import { ChangeEvent, useRef, useState } from 'react';

type Props = {
  className?: string;
  onImageChange: (image: File) => void;
};

const DEFAULT_IMAGE_URL = '/form-image-add.png';

function ImageUploadButton({ className = '', onImageChange }: Props) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [previewUrl, setPreviewUrl] = useState<string>(DEFAULT_IMAGE_URL); // 기본 이미지

  const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files === null) return;
    const image = e.target.files[0];
    if (!image) return;
    onImageChange(image);

    // 미리보기 URL 생성
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreviewUrl(reader.result as string);
    };
    reader.readAsDataURL(image);
  };

  const handleUploadClick = () => {
    if (!inputRef.current) return;
    inputRef.current.click();
  };

  return (
    <div className={`flex h-full w-full cursor-pointer items-center justify-center rounded-2xl ${className}`}>
      <img src={previewUrl} alt='Preview' className='h-full w-full rounded-2xl bg-white object-contain shadow-lg' onClick={handleUploadClick} />
      <input type='file' accept='image/png, image/jpeg' className='hidden' ref={inputRef} onChange={handleImageChange} />
    </div>
  );
}

export default ImageUploadButton;

