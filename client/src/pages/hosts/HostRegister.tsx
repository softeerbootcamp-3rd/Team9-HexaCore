import Button from '@/components/Button';
import TitledBlock from '@/components/TitledBlock';
import ImageUploadButton from '@/pages/hosts/ImageUploadButton';
import { KeyboardEvent, useEffect, useRef, useState } from 'react';
import { useLoaderData, useNavigate } from 'react-router-dom';
import AddressButton from './AddressButton';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';
import axios from 'axios';
import { HostRegisterLoaderData } from './hostsRoutes';
import LoadingCircle from '@/components/svgs/LoadingCircle';
import { useCustomToast } from '@/components/Toast';
import { uploadToS3WithDownscaled } from '@/utils/ImageManger';
import { CreateCarReuqestParam, UpdateCarRequestParam } from '@/fetches/cars/cars.type';

const GET_CAR_INFO_API_URL = 'https://datahub-dev.scraping.co.kr/assist/common/carzen/CarAllInfoInquiry';

interface CarDetailResponseByApi {
  CARNAME: string;
  CARYEAR: string;
  FUEL: string;
  FUELECO: string;
  SEATS: string;
  CC: string;
}

type CarDetailByApi = {
  capacity: number;
  carName: string;
  carNumber: string;
  fuel: string;
  mileage: number;
  type: string;
  year: number;
};

type positionLatLng = {
  lat: string;
  lng: string;
};

// 데이터허브 API의 연료 형식을 DB에 맞게 변경한다.
const convertFuelType = (fuel: string) => {
  if (fuel === '가솔린') return '휘발유';
  if (fuel === '디젤') return '경유';
  if (fuel === 'LPG') return 'LPG';
  if (fuel === '전기') return '전기';
  if (fuel === '수소전기') return '수소';

  return '';
};

// 배기량을 기준으로 차량 종류를 구별한다.
const calculateCarType = (CC: string) => {
  const CCvalue = Number(CC);
  if (CCvalue < 1000) {
    return '경차';
  }
  if (CCvalue < 1300) {
    return '소형차';
  }
  if (CCvalue < 1600) {
    return '준중형차';
  }
  if (CCvalue < 2000) {
    return '중형차';
  }
  return '대형차';
};

function HostRegister() {
  const [carDetail, setCarDetail] = useState<CarDetailByApi | null>(null);
  const [images, setImages] = useState<(File | null)[]>([null, null, null, null, null]);
  const [description, setDescription] = useState<string>('');
  const [fee, setFee] = useState<string>('');
  const [address, setAddress] = useState<string>('');
  const [position, setPosition] = useState<positionLatLng | null>(null);
  const [isCarNumberConfirmed, setCarNumberConfirmed] = useState<boolean>(false);
  const [loadingCarNumber, setLoadingCarNumber] = useState<boolean>(false);
  const [imageMessage, setImageMessage] = useState<string | null>(null);
  const [feeMessage, setFeeMessage] = useState<string | null>(null);
  const [addressMessage, setAddressMessage] = useState<string | null>(null);
  const feeRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const userCarInfo = useLoaderData() as HostRegisterLoaderData;

  const { ToastComponent, showToast } = useCustomToast();

  // 만약 수정에 대한 사항이라면 차량 번호 조회 페이지를 생략한다.
  useEffect(() => {
    setCarDetail(null);
    setCarNumberConfirmed(false);

    if (userCarInfo.isUpdate && userCarInfo.carDetail) {
      const carDetail = userCarInfo.carDetail;
      setAddress(carDetail.address);
      setDescription(carDetail.description);
      setFee(carDetail.feePerHour.toLocaleString('ko-kr'));

      setCarDetail({
        capacity: carDetail.capacity,
        carName: carDetail.carName,
        carNumber: carDetail.carNumber,
        fuel: carDetail.fuel,
        mileage: carDetail.mileage,
        type: carDetail.type,
        year: carDetail.year,
      });

      setCarNumberConfirmed(true);
    }
  }, [userCarInfo]);

  // 주소의 변경시 좌표 API를 호출하여 결과를 가진다.
  useEffect(() => {
    if (address == '') return;
    naver.maps.Service.geocode(
      {
        query: address,
      },
      (status, resp) => {
        if (status !== naver.maps.Service.Status.OK) {
          return;
        }
        const result = resp.v2.addresses[0];
        setPosition({ lat: result.y, lng: result.x });
      },
    );
  }, [address]);

  // 서버에 접근할 수 없거나 요청에 대한 응답에 오류가 발생할 경우
  if (userCarInfo === null) {
    return <div>{'서버에 연결할 수 없습니다. 다시 시도해 주세요.'}</div>;
  }
  // 인증이 안된 경우 빈 페이지를 보여준다. -> 로그인 페이지로 리다이렉트
  if (userCarInfo.errMessage === '로그인이 필요한 요청입니다.') {
    return <div></div>;
  }

  const validateNumber = (e: KeyboardEvent<HTMLInputElement>) => {
    const pattern = /^([0-9]|Backspace|ArrowLeft|ArrowRight)+$/; // 숫자, 백스페이스, 좌우 방향키를 허용하는 정규식
    const isValidInput = pattern.test(e.key);

    if (!isValidInput) {
      e.preventDefault();
    }
  };

  // 입력된 대여료를 포매팅한다.
  const formatCurrency = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.currentTarget.value.length === 0 || !feeRef.current) return;

    const currencyValue = fee ?? 0; // NaN일때 0으로 변환
    const currencyValueNumber = Number(currencyValue.replace(/,/g, ''));

    // 0 혹은 NaN이면 빈 칸으로 바꾼다.
    if (currencyValueNumber === 0 || Number.isNaN(currencyValueNumber)) {
      setFee('');
      return;
    }

    const currencyValueFilledZero = currencyValueNumber >= 1000 ? currencyValueNumber : currencyValueNumber * 1000;
    const formattedCurrency = currencyValueFilledZero.toLocaleString('ko-KR');
    // 0 3개마다 , 표시

    if (feeRef.current) {
      setFee(formattedCurrency);
      // 입력 창의 커서를 ",000"의 앞의 위치로 강제한다.
      feeRef.current.selectionStart = e.currentTarget.value.length - 4;
      feeRef.current.selectionEnd = e.currentTarget.value.length - 4;
    }
  };

  const changeImageByIndex = (index: number, image: File) => {
    setImages((images) => {
      const newImages = [...images];
      newImages[index] = image;
      return newImages;
    });
  };

  const onSubmitCheckCarNumber = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    showToast('', '', false, 0);

    const formData = new FormData(e.currentTarget);
    const registerNumber = formData.get('REGINUMBER')?.toString();

    if (registerNumber === undefined || registerNumber === '') {
      showToast('차량번호 미입력', '차량번호는 반드시 입력해야 합니다!');
      return;
    }

    if (!registerNumber.match(/^[0-9]{2,3}[가-힣][0-9]{4}$/g)) {
      showToast('차량번호 형식이 아님', '차량번호의 형식이 아닙니다. (예시: 12가1234)');
      return;
    }

    setLoadingCarNumber(true);
    const response = await axios
      .post(
        GET_CAR_INFO_API_URL,
        { REGINUMBER: registerNumber, OWNERNAME: userCarInfo.username },
        { headers: { Authorization: `Token ${import.meta.env.VITE_CAR_INFO_API_TOKEN}` } },
      )
      .finally(() => setLoadingCarNumber(false));

    const resultMessage = response.data.result;
    const responseData: CarDetailResponseByApi = response.data.data;

    if (resultMessage == 'SUCCESS') {
      const responseCarName = responseData.CARNAME;
      const responseCarYear = responseData.CARYEAR;
      const responseCarFuel = responseData.FUEL;
      const responseCarMileage = responseData.FUELECO;
      const responseCarCapacity = responseData.SEATS;

      setCarDetail({
        capacity: Number.parseInt(responseCarCapacity),
        carName: responseCarName,
        carNumber: registerNumber,
        fuel: convertFuelType(responseCarFuel),
        mileage: Number.parseFloat(responseCarMileage),
        type: calculateCarType(responseData.CC),
        year: Number.parseInt(responseCarYear),
      });

      setCarNumberConfirmed(true);
    } else {
      showToast('등록번호로 차량조회 실패', '등록번호로 조회에 실패하였습니다. 다시 시도해주세요.');
    }
  };

  const formDataToPostJson = (formData: FormData) => {
    const createCarReuqestParam: CreateCarReuqestParam = {
      carNumber: formData.get('carNumber') as string,
      carName: formData.get('carName') as string,
      mileage: Number(formData.get('mileage')),
      fuel: formData.get('fuel') as string,
      type: formData.get('type') as string,
      capacity: Number(formData.get('capacity')),
      year: Number(formData.get('year')),
      feePerHour: Number(formData.get('feePerHour')),
      address: formData.get('address') as string,
      position: {
        lat: formData.get('position.lat') as string,
        lng: formData.get('position.lng') as string,
      },
      description: formData.get('description') as string,
      imageUrls: formData.getAll('imageUrls').map((url) => url.toString()),
      imageIndexes: formData.getAll('imageIndexes').map((idx) => idx.toString()),
    };
    return createCarReuqestParam;
  };

  const formDataToUpdateJson = (formData: FormData) => {
    const updateCarRequestParam: UpdateCarRequestParam = {
      feePerHour: Number(formData.get('feePerHour')),
      address: formData.get('address') as string,
      position: {
        lat: formData.get('position.lat') as string,
        lng: formData.get('position.lng') as string,
      },
      description: formData.get('description') as string,
      imageUrls: formData.getAll('imageUrls').map((url) => url.toString()),
      imageIndexes: formData.getAll('imageIndexes').map((idx) => idx.toString()),
    };
    return updateCarRequestParam;
  };

  // TODO
  const onSubmitRequestCarRegister = async () => {
    if (feeRef.current === null) return;
    setFeeMessage(null);
    setImageMessage(null);
    setAddressMessage(null);
    showToast('', '', false, 0);

    const formData = new FormData();
    let formFailed = false;
    let feeValue: number = 0;

    if (carDetail === null) {
      setCarNumberConfirmed(false);
      showToast('차량 조회 필요', '차량 조회가 되지 않은 상태입니다.');
      return;
    }

    if (!userCarInfo.isUpdate && images.length != 5) {
      setImageMessage('등록 시 차량 사진은 반드시 5장을 제출해야합니다.');
      formFailed = true;
    }

    const imageUploadPromises = images.map((image, index) => {
      if (image !== null) {
        return uploadToS3WithDownscaled(image).then((imageUrl) => {
          if (imageUrl) {
            return { imageUrl, index };
          }
        });
      }
      return Promise.resolve(null);
    });

    const uploadedImages = await Promise.all(imageUploadPromises);
    uploadedImages.forEach((imageData) => {
      if (imageData) {
        formData.append('imageUrls', imageData.imageUrl);
        formData.append('imageIndexes', imageData.index.toString());
      }
    });

    if (feeRef.current.validity.valueMissing) {
      setFeeMessage('대여료는 필수로 입력하셔야 합니다.');
      formFailed = true;
    } else {
      feeValue = Number(feeRef.current.value.replace(/,/g, ''));
      const isRangeUnderflow = feeValue < 1000;
      const isStepMismatch = feeValue % 1000 != 0;

      if (isRangeUnderflow || isStepMismatch) {
        setFeeMessage('대여료는 1000원 이상, 단위는 1000원입니다. 다시 입력해주세요.');
        formFailed = true;
      }
    }

    if (address == '') {
      setAddressMessage('차량 위치를 입력해주세요.');
      formFailed = true;
    }

    if (position === null) {
      setAddressMessage('차량 위치를 다시 입력해주세요.');
      formFailed = true;
    }

    if (formFailed) return;

    if (!userCarInfo.isUpdate) {
      // 차량 등록에 경우에만 해당 정보를 입력한다.
      formData.append('carName', carDetail.carName);
      formData.append('carNumber', carDetail.carNumber);
      formData.append('mileage', carDetail.mileage.toString());
      formData.append('fuel', carDetail.fuel);
      formData.append('type', carDetail.type);
      formData.append('capacity', carDetail.capacity.toString());
      formData.append('year', carDetail.year.toString());
    }
    formData.append('feePerHour', feeValue.toString());
    formData.append('address', address);
    if (position) {
      formData.append('position.lat', position.lat);
      formData.append('position.lng', position.lng);
    }
    formData.append('description', description);

    let response: ResponseWithoutData;

    if (userCarInfo.isUpdate) {
      response = await server.put<ResponseWithoutData>(`/cars/${userCarInfo.carDetail?.id}`, {
        data: formDataToUpdateJson(formData),
      });
    } else {
      response = await server.post<ResponseWithoutData>('/cars', {
        data: formDataToPostJson(formData),
      });
    }

    const currentActionMesssage = `차량 ${userCarInfo.isUpdate ? '수정' : '등록'}`;

    const resetInfo = () => {
      setCarDetail(null);
      setAddress('');
      setDescription('');
      setFee('');
      setImages([null, null, null, null, null]);
      setCarNumberConfirmed(false);
    };

    if (!response.success) {
      if (response.message === '존재하지 않는 모델명입니다.') {
        showToast('미지원 차종', '타요 서비스에 사용할 수 없는 차량입니다. 다른 차량으로 시도해 주세요.');
        resetInfo();
        return;
      }
      if (response.message === '중복된 차량 번호입니다.') {
        showToast(`이미 등록된 차량`, `이미 등록된 차량입니다. 다른 차량으로 시도해 주세요.`);
        resetInfo();
        return;
      }

      showToast(`${currentActionMesssage} 실패`, `${currentActionMesssage}을 실패하였습니다. 다시 시도해 주세요.`);
      return;
    }

    showToast(`${currentActionMesssage} 완료`, `${currentActionMesssage}을 완료하였습니다.`, true);
    navigate('/hosts/manage');
  };

  if (!isCarNumberConfirmed)
    return (
      <div className={`relative flex h-full w-full flex-col`}>
        <div className='h-1/5' />
        <div>
          <h1 className='mb-7 text-center text-5xl font-semibold'>{'Hello'}</h1>
          <div className='my-5'>
            <h4 className='text-center text-background-400'>{'스마트한 내차 빌려주기, 시작해볼까요?'}</h4>
            <h4 className='text-center text-background-400'>{'정확한 차량번호를 입력해주세요.'}</h4>
          </div>
          <form method='POST' action='https://datahub-dev.scraping.co.kr/assist/common/carzen/CarAllInfoInquiry' onSubmit={onSubmitCheckCarNumber}>
            <div className='flex justify-center'>
              <div className='flex w-5/12 items-center justify-between rounded-3xl bg-white px-6 py-2'>
                <input className='w-full p-3 text-2xl focus:outline-none' name='REGINUMBER' type='text' placeholder='12가3456' autoComplete='off' />
                <input type='image' src='/search-button.png' width={48} height={48} />
              </div>
            </div>
          </form>
        </div>
        <div className='mt-8 flex flex-col items-center'>
          <div
            id='loading_circle'
            className={`${loadingCarNumber ? '' : 'hidden'} z-50 flex w-4/12 items-center justify-center gap-6 rounded-2xl bg-background-600 px-8 py-4 opacity-50`}>
            <LoadingCircle />
            <div className='text-white'>{'차량정보를 불러오는 중입니다...'}</div>
          </div>
          <ToastComponent />
        </div>
      </div>
    );
  return (
    <div className='flex min-h-full flex-col justify-between gap-5'>
      <div className='grid grid-cols-1 gap-8 md:grid-cols-5'>
        <div className='col-span-2 flex flex-col gap-4'>
          <TitledBlock title='차량 정보'>
            <div className='rounded-3xl bg-white p-5 shadow-lg'>
              <p className='text-lg text-background-400'>
                {carDetail?.carName} {carDetail?.carNumber}
              </p>
              <p className='text-lg text-background-400'>{`${carDetail?.type} | ${carDetail?.capacity}인승 | ${carDetail?.fuel} | ${carDetail?.mileage} km / L`}</p>
            </div>
          </TitledBlock>
          <TitledBlock title='부가 설명' className='flex grow flex-col'>
            <p className='mb-1 text-sm text-background-400'>{'차에 대한 부가 정보나, 차를 사용할 때 주의점을 적어주세요.'}</p>
            <div className='min-h-40 grow rounded-3xl bg-white p-5 shadow-lg'>
              <textarea
                value={description}
                onChange={(e) => {
                  setDescription(e.target.value);
                }}
                name='carAdditionalInfo'
                placeholder='차량을 소중히 운전해주세요.'
                className='h-full w-full resize-none placeholder:text-background-200 focus:outline-none'></textarea>
            </div>
          </TitledBlock>
        </div>

        <div className='col-span-3 flex flex-col gap-4'>
          <div className='flex w-full flex-wrap gap-2'>
            <TitledBlock title='차량 위치' className='flex-1'>
              <p className={`mb-1 text-sm ${addressMessage === null ? 'text-background-400' : 'text-danger-400'}`}>
                {addressMessage ?? '차를 빌린 사용자가 차량을 픽업할 위치를 입력해주세요.'}
              </p>
              <AddressButton
                address={address}
                setAddress={setAddress}
                buttonImgUrl='/search-button.png'
                className='flex cursor-pointer items-center justify-between rounded-3xl bg-white px-3 py-1 shadow-lg'
              />
            </TitledBlock>
            <TitledBlock title='대여료' className='flex-1'>
              <p className={`mb-1 text-sm ${feeMessage === null ? 'text-background-400' : 'text-danger-400'}`}>
                {feeMessage ?? '사용자에게 시간 당 얼마를 받을지 요금을 입력해주세요.'}
              </p>
              <div id='carFee'>
                <div className='flex items-center justify-between rounded-3xl bg-white px-3 py-1 shadow-lg'>
                  <input
                    className='w-full p-3 text-xl placeholder:text-background-200 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none'
                    name='Fee'
                    type='text'
                    placeholder='100,000'
                    onKeyDown={validateNumber}
                    onKeyUp={formatCurrency}
                    onSelect={formatCurrency}
                    ref={feeRef}
                    value={fee}
                    onChange={(e) => {
                      setFee(e.target.value);
                    }}
                    maxLength={20}
                    required
                  />
                  <p className='text-semibold min-w-16 text-lg text-background-400'>{'원 / 시간'}</p>
                </div>
              </div>
            </TitledBlock>
          </div>
          <TitledBlock title='사진 등록'>
            <div className='flex flex-col'>
              <p className={`mb-1 text-sm ${imageMessage === null ? 'text-background-400' : 'text-danger-400'}`}>
                {imageMessage ?? '차량 정면, 후면, 운전석 쪽, 보조석 쪽, 내부 사진 등을 올려주세요.'}
              </p>
              <div id='carImages' className='flex h-[450px] min-h-96 flex-wrap content-start gap-2'>
                <ImageUploadButton onImageChange={(image) => changeImageByIndex(0, image)} className='flex-1' imageUrl={userCarInfo.carDetail?.imageUrls[0]} />
                <div className='grid h-full flex-1 grid-cols-2 grid-rows-2 gap-2'>
                  {Array.from({ length: 4 }, (_, index) => (
                    <ImageUploadButton
                      key={index}
                      onImageChange={(image) => changeImageByIndex(index + 1, image)}
                      imageUrl={userCarInfo.carDetail?.imageUrls[index + 1]}
                    />
                  ))}
                </div>
              </div>
            </div>
          </TitledBlock>
        </div>
      </div>
      <div className='mb-8 flex justify-end'>
        <ToastComponent />
        <Button text='등록하기' onClick={onSubmitRequestCarRegister} />
      </div>
    </div>
  );
}

export default HostRegister;

