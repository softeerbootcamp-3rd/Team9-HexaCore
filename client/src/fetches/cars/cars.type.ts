// ---- Response
export type CarData = {
  id: number;
  carName: string;
  imageUrl: string;
  carAddress: string;
  mileage: number;
  capacity: number;
  feePerHour: number;
};

// ---- Request
export type PageRequestParam = {
  page?: number; // 요청 페이지 넘버
  size?: number; // 페이지 사이즈
  sort?: string; // 정렬 기준 "id,DESC"
};

export type CarSearchParam = PageRequestParam & {
  lat: number; // 위도
  lng: number; // 경도
  startDate: string; // 대여일
  endDate: string; // 반납일
  party: string; // 탑승 인원
  type?: string; // 차종
  category?: string; // 모델명
  subcategory?: string; // 세부 모델명
  minPrice?: number; // 최저가격
  maxPrice?: number; // 최고가격
  page?: number;
  size?: number;
  sort?: string;
};

