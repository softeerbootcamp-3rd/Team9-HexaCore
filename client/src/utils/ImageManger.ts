import { fetchPresignedUrls } from '@/fetches/cars/fetchCars';
import axios from 'axios';

const RESIZE_WIDTH = 100;
const BUCKET_NAME = import.meta.env.VITE_S3_BUCKET_NAME;
export const DOWNSCALE_PREFIX = 'downscaled_';

export const uploadToS3 = async (file: File) => {
  // Presigned URL 요청을 위한 파일 이름과 타입 설정
  const fileType = file.type;
  const fileName = `${Date.now()}-${file.name}`;

  // Presigned URL 가져오기
  const presignedUrlData = await fetchPresignedUrls({ fileName, fileType });

  if (!presignedUrlData) {
    return '';
  }

  try {
    // S3에 이미지 업로드
    const newFile = new File([file], fileName, { type: file.type });
    await axios.put(presignedUrlData.originalPresignedUrl, newFile, {
      headers: {
        'Content-Type': file.type,
      },
    });

    // S3에 업로드된 이미지 URL 리턴
    return `https://${BUCKET_NAME}.s3.ap-northeast-2.amazonaws.com/${fileName}`;
  } catch (error) {
    return '';
  }
};

export const uploadToS3WithDownscaled = async (file: File) => {
  // Presigned URL 요청을 위한 파일 이름과 타입 설정
  const fileType = file.type;
  const fileName = `${Date.now()}-${file.name}`;

  // Presigned URL 가져오기
  const presignedUrlData = await fetchPresignedUrls({ fileName, fileType, prefix: DOWNSCALE_PREFIX });

  if (!presignedUrlData || presignedUrlData.downscaledPresignedURl === null) {
    return '';
  }

  try {
    // S3에 이미지 업로드
    const newFile = new File([file], fileName, { type: file.type });
    await axios.put(presignedUrlData.originalPresignedUrl, newFile, {
      headers: {
        'Content-Type': file.type,
      },
    });
    const downscaledFile = await downscaleFile(file);
    const newDownscaledFile = new File([downscaledFile], DOWNSCALE_PREFIX + fileName, { type: file.type });
    await axios.put(presignedUrlData.downscaledPresignedURl, newDownscaledFile, {
      headers: {
        'Content-Type': file.type,
      },
    });

    // S3에 업로드된 이미지 URL 리턴
    return `https://${BUCKET_NAME}.s3.ap-northeast-2.amazonaws.com/${fileName}`;
  } catch (error) {
    return '';
  }
};

export const addDownscaledPrefix = (imageUrl: string): string => {
  const parts = imageUrl.split('/');
  const fileName = parts.pop();

  if (fileName) {
    parts.push(`${DOWNSCALE_PREFIX}${fileName}`);
  }

  return parts.join('/');
};

const downscaleFile = (image: File): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        try {
          const thumbFile = getThumbFile(img); // 썸네일 생성
          resolve(thumbFile);
        } catch (error) {
          reject(error);
        }
      };
      img.onerror = () => {
        reject(new Error('이미지 로드 실패'));
      };
      if (e.target?.result) {
        img.src = e.target.result.toString();
      }
    };
    reader.onerror = () => {
      reject(new Error('파일 읽기 실패'));
    };
    reader.readAsDataURL(image); // 파일 객체를 Data URL로 읽기
  });
};

function getThumbFile(image: HTMLImageElement): Blob {
  const canvas = document.createElement('canvas');
  const ctx = canvas.getContext('2d');

  const ratio = image.height / image.width; // 원본 이미지의 비율 계산
  const height = RESIZE_WIDTH * ratio; // 너비에 따라 높이를 비율에 맞게 조정

  canvas.width = RESIZE_WIDTH;
  canvas.height = height;

  if (ctx) {
    ctx.drawImage(image, 0, 0, RESIZE_WIDTH, height);
  }

  const dataURI = canvas.toDataURL('image/png');
  const byteString = atob(dataURI.split(',')[1]);
  const mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
  const ab = new ArrayBuffer(byteString.length);
  const ia = new Uint8Array(ab);
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }

  return new Blob([ab], { type: mimeString });
}

