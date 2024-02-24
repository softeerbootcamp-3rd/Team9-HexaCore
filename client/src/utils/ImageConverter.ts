const RESIZE_WIDTH = 100;

export const downscaleImage = (image: HTMLImageElement) => {
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
  
    const ratio = image.height / image.width; // 원본 이미지의 비율 계산
    const height = RESIZE_WIDTH * ratio; // 너비에 따라 높이를 비율에 맞게 조정
  
    canvas.width = RESIZE_WIDTH;
    canvas.height = height;
  
    if (ctx) {
      ctx.drawImage(image, 0, 0, RESIZE_WIDTH, height);
    }
  
    // canvas의 data URL을 blob(file)로 변환하는 과정
    const dataURI = canvas.toDataURL("image/png"); // png => jpg 등으로 변환 가능
    const byteString = atob(dataURI.split(',')[1]);
    const mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
  
    // 리사이징된 file 객체
    const tmpThumbFile = new Blob([ab], { type: mimeString });
  
    return tmpThumbFile;
};

export const downscaleFile = (image: File): Promise<Blob> => {
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
          reject(new Error("이미지 로드 실패"));
        };
        if (e.target?.result) {
          img.src = e.target.result.toString();
        }
      };
      reader.onerror = () => {
        reject(new Error("파일 읽기 실패"));
      };
      reader.readAsDataURL(image); // 파일 객체를 Data URL로 읽기
    });
  };
  
  function getThumbFile(_IMG: HTMLImageElement): Blob {
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
  
    const width = 100; // 리사이징할 가로 길이
    const height = 100; // 리사이징할 세로 길이
  
    canvas.width = width;
    canvas.height = height;
  
    if (ctx) {
      ctx.drawImage(_IMG, 0, 0, width, height);
    }
  
    const dataURI = canvas.toDataURL("image/png");
    const byteString = atob(dataURI.split(',')[1]);
    const mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
  
    return new Blob([ab], {type: mimeString});
  }
  