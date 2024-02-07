import { ChangeEvent, useState } from 'react';

type Props = {
  imageInputRef: React.RefObject<HTMLInputElement>;
  isLargeButton?: boolean;
};

function ImageUploadButton({ imageInputRef, isLargeButton = false }: Props) {
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('/form-image-add.png'); // 기본 이미지

  const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const image = e.target.files && e.target.files[0];
    if (image) {
      setSelectedImage(image);

      // 미리보기 URL 생성
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(image);
    }
  };

  const handleUploadClick = () => {
    if (imageInputRef.current) {
      imageInputRef.current.click();
    }
  };

  const buttonClassName: string = `flex ${isLargeButton ? 'h-72 w-72' : 'h-32 w-32'} cursor-pointer items-center justify-center rounded-2xl bg-white`;
  const imageClassName: string = `h-full w-full rounded-2xl`;

  return (
    <>
      <input type="file" accept="image/png, image/jpeg" className="hidden" ref={imageInputRef} onChange={handleImageChange} />
      <div className={buttonClassName} onClick={handleUploadClick}>
        <img src={previewUrl} alt="Preview" className={imageClassName} />
      </div>
    </>
  );
}

export default ImageUploadButton;

