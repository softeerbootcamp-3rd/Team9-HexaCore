import { ChangeEvent, useRef, useState } from 'react';

type Props = {
  buttonClassName?: string;
  imageClassName?: string;
  isLargeButton?: boolean;
  onImageChange: (image: File) => void;
};

function ImageUploadButton({ isLargeButton = false, buttonClassName = '', imageClassName = '', onImageChange }: Props) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('/form-image-add.png'); // 기본 이미지

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
    <>
      <input type="file" accept="image/png, image/jpeg" className="hidden" ref={inputRef} onChange={handleImageChange} />
      <div
        className={`flex cursor-pointer items-center justify-center rounded-2xl 
          ${isLargeButton ? 'h-full w-full py-2 pr-2' : 'h-1/2 w-1/2 p-2'}
          ${buttonClassName}`}>
        <img src={previewUrl} alt="Preview" className={`h-full w-full rounded-2xl bg-white object-contain ${imageClassName}`} onClick={handleUploadClick} />
      </div>
    </>
  );
}

export default ImageUploadButton;

