import { ChangeEvent, useState } from 'react';

type Props = {
  imageInputRef: React.RefObject<HTMLInputElement>;
  buttonClassName?: string | null;
  imageClassName?: string | null;
  isLargeButton?: boolean;
};

function ImageUploadButton({ imageInputRef, isLargeButton = false, buttonClassName = null, imageClassName = null }: Props) {
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

  buttonClassName =
    buttonClassName ?? `flex ${isLargeButton ? 'h-full w-full py-2 pr-2' : 'h-1/2 w-1/2 p-2'} cursor-pointer items-center justify-center rounded-2xl`;
  imageClassName = imageClassName ?? `h-full w-full rounded-2xl bg-white object-contain`;

  return (
    <>
      <input type="file" accept="image/png, image/jpeg" className="hidden" ref={imageInputRef} onChange={handleImageChange} />
      <div className={buttonClassName}>
        <img src={previewUrl} alt="Preview" className={imageClassName} onClick={handleUploadClick} />
      </div>
    </>
  );
}

export default ImageUploadButton;

