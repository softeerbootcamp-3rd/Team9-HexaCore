import Button from '@/components/Button';
import TitledBlock from '@/components/TitledBlock';
import ImageUploadButton from '@/pages/hosts/ImageUploadButton';
import { KeyboardEvent, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddressButton from './AddressButton';
import { server } from '@/fetches/common/axios';
import { ResponseWithoutData } from '@/fetches/common/response.type';

type positionLatLng = {
  lat: string;
  lng: string;
};

function HostRegister() {
  const [images, setImages] = useState<(File | null)[]>([null, null, null, null, null]);
  const descriptionRef = useRef<HTMLTextAreaElement>(null);
  const [address, setAddress] = useState<string>('');
  const [position, setPosition] = useState<positionLatLng | null>(null);
  const [isCarNumberConfirmed, setCarNumberConfirmed] = useState<boolean>(false);
  const [imageMessage, setImageMessage] = useState<string | null>(null);
  const [feeMessage, setFeeMessage] = useState<string | null>(null);
  const [addressMessage, setAddressMessage] = useState<string | null>(null);
  const feeRef = useRef<HTMLInputElement>(null);
  const navigator = useNavigate();

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

    const currencyValue = e.currentTarget.value ?? 0; // NaN일때 0으로 변환
    const currencyValueNumber = Number(currencyValue.replace(/,/g, ''));

    // 0 혹은 NaN이면 빈 칸으로 바꾼다.
    if (currencyValueNumber === 0 || Number.isNaN(currencyValueNumber)) {
      feeRef.current.value = '';
      return;
    }

    const currencyValueFilledZero = currencyValueNumber >= 1000 ? currencyValueNumber : currencyValueNumber * 1000;
    const formattedCurrency = currencyValueFilledZero.toLocaleString('ko-KR');
    // 0 3개마다 , 표시

    if (feeRef.current) {
      feeRef.current.value = formattedCurrency;
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

  const onSubmitCheckCarNumber = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formData = new FormData(e.currentTarget);

    if (formData.get('REGINUMBER') != '') {
      setCarNumberConfirmed(true);
      // TODO: 외부 API에 fetch 등으로 요청을 보낸 뒤, 응답으로 받은 차량 정보를 저장한다.
    } else {
      alert('등록번호를 잘못 입력하셨습니다!');
    }
  };

  const onSubmitRequestCarRegister = async () => {
    if (descriptionRef.current === null || feeRef.current === null) return;
    setFeeMessage(null);
    setImageMessage(null);
    setAddressMessage(null);

    const formData = new FormData();
    let formFailed = false;
    let feeValue: number = 0;

    images.forEach((image) => {
      if (image !== null) formData.append(`imageFiles`, image);
    });

    if (formData.getAll('imageFiles').length != 5) {
      setImageMessage('차량 사진은 반드시 5장을 제출해야합니다.');
      formFailed = true;
    }

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

    if (position === null) formFailed = true;

    if (formFailed) return;

    formData.append('carNumber', '12가2345');
    formData.append('carName', 'SM5');
    formData.append('mileage', (12).toString());
    formData.append('fuel', '휘발유');
    formData.append('type', '중형차');
    formData.append('capacity', (5).toString());
    formData.append('year', (2020).toString());
    formData.append('feePerHour', feeValue.toString());
    formData.append('address', address);

    if (position) {
      formData.append('position.lat', position.lat);
      formData.append('position.lng', position.lng);
    }
    formData.append('description', descriptionRef.current.value);
    formData.append('imageIndexes', Array.of(0, 1, 2, 3, 4).toString());
    const response = await server.post<ResponseWithoutData>('/cars', {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      data: formData,
    });

    if (response.success) {
      alert('차량 등록을 완료하였습니다.');
      navigator('/hosts/manage');
    }
    alert('차량 등록에 실패하였습니다. 다시 시도해 주세요.');
  };

  if (!isCarNumberConfirmed)
    return (
      <div className='flex h-full flex-col'>
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
                <input className='w-full p-3 text-2xl focus:outline-none' name='REGINUMBER' type='text' placeholder='12가 3456' />
                <input type='image' src='/search-button.png' width={48} height={48} />
              </div>
            </div>
          </form>
        </div>
      </div>
    );

  return (
    <div className='flex min-h-full flex-col justify-between gap-5'>
      <div className='grid grid-cols-1 gap-8 md:grid-cols-5'>
        <div className='col-span-2 flex flex-col gap-4'>
          <TitledBlock title='차량 정보'>
            <div className='rounded-3xl bg-white p-5 shadow-lg'>
              <p className='text-lg text-background-400'>{'제네시스 G80 12가 3456'}</p>
              <p className='text-lg text-background-400'>{'대형차 | 5인승 | 연료 | 연비'}</p>
            </div>
          </TitledBlock>
          <TitledBlock title='부가 설명' className='flex grow flex-col'>
            <p className='mb-1 text-sm text-background-400'>{'차에 대한 부가 정보나, 차를 사용할 때 주의점을 적어주세요.'}</p>
            <div className='min-h-40 grow rounded-3xl bg-white p-5 shadow-lg'>
              <textarea
                ref={descriptionRef}
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
                    maxLength={20}
                    ref={feeRef}
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
                <ImageUploadButton onImageChange={(image) => changeImageByIndex(0, image)} className='flex-1' />
                <div className='grid h-full flex-1 grid-cols-2 grid-rows-2 gap-2'>
                  {Array.from({ length: 4 }, (_, index) => (
                    <ImageUploadButton key={index} onImageChange={(image) => changeImageByIndex(index + 1, image)} />
                  ))}
                </div>
              </div>
            </div>
          </TitledBlock>
        </div>
      </div>
      <div className='mb-8 flex justify-end'>
        <Button text='등록하기' onClick={onSubmitRequestCarRegister} />
      </div>
    </div>
  );
}

export default HostRegister;

