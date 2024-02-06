import ImageUploadButton from '@/pages/hosts/ImageUploadButton';
import { KeyboardEvent, useRef, useState } from 'react';

function HostRegister() {
  const imageInputRefs = Array.from({ length: 5 }, () => useRef<HTMLInputElement>(null));
  const descriptionRef = useRef<HTMLTextAreaElement>(null);
  const feeRef = useRef<HTMLInputElement>(null);
  const [isCarNumberConfirmed, setCarNumberConfirmed] = useState<boolean>(false);

  const validateNumber = (e: KeyboardEvent<HTMLInputElement>) => {
    const pattern = /^[0-9|Backspace|ArrowLeft|ArrowRight]+$/; // 숫자, 백스페이스, 좌우 방향키를 허용하는 정규식
    const isValidInput = pattern.test(e.key);

    if (!isValidInput) {
      e.preventDefault();
    }
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
    const formData = new FormData();

    imageInputRefs.forEach((imageInputRef) => {
      const imageInput = imageInputRef.current;
      if (imageInput && imageInput.files && imageInput.files.length > 0) {
        // 각 이미지 파일을 FormData에 추가
        formData.append('images', imageInput.files[0]);
      }
    });

    if (descriptionRef.current) formData.append('description', descriptionRef.current.value);
    if (feeRef.current) formData.append('feePerHour', feeRef.current.value);

    // TODO: 서버에 차량 등록 요청을 하고 성공/실패 여부에 따라 리다이렉트한다.
    // carNumber, carName, address, position 등의 속성은 외부 API를 사용한다.
  };

  const CarNumberForm = (
    <>
      <h1 className="my-7 text-center text-5xl font-semibold">{'Hello'}</h1>
      <div className="my-5">
        <h4 className="text-center text-background-400">{'스마트한 내차 빌려주기, 시작해볼까요?'}</h4>
        <h4 className="text-center text-background-400">{'정확한 차량번호를 입력해주세요.'}</h4>
      </div>
      <form method="POST" action="https://datahub-dev.scraping.co.kr/assist/common/carzen/CarAllInfoInquiry" onSubmit={onSubmitCheckCarNumber}>
        <div className="flex justify-center">
          <div className="flex w-5/12 items-center justify-between rounded-3xl bg-white px-6 py-2">
            <input className="w-full p-3 text-2xl focus:outline-none" name="REGINUMBER" type="text" placeholder="12가 3456" />
            <input type="image" src="/search-button.png" width={48} height={48} />
          </div>
        </div>
      </form>
    </>
  );

  const HostRegisterForm = (
    <>
      <div className="flex min-w-full justify-center">
        <div className="mr-12 flex min-h-80 w-4/12 min-w-40 flex-col justify-between">
          <div id="carInfoSection">
            <p className="my-2 text-xl font-semibold">{'차량 정보'}</p>
            <div id="carInfo" className="flex h-24 flex-col items-start justify-center rounded-lg bg-white p-5">
              <p className="text-background-400">{'제네시스 G80 12가 3456'}</p>
              <p className="text-background-400">{'대형차 | 5인승 | 연료 | 연비'}</p>
            </div>
          </div>
          <div id="additionalInfoSection">
            <p className="mb-2 mt-4 text-xl font-semibold">{'부가 설명'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차에 대한 부가 정보나, 차를 사용할 때 주의점을 적어주세요.'}</h6>
            <div id="additionalInfo" className="h-96 min-h-40 rounded-lg bg-white p-4">
              <textarea
                ref={descriptionRef}
                name="carAdditionalInfo"
                placeholder="차량을 소중히 운전해주세요."
                className="h-full min-h-60 w-full resize-none placeholder:text-background-200 focus:outline-none"></textarea>
            </div>
          </div>
        </div>
        <div className="flex w-6/12 min-w-80 flex-col justify-between">
          <div>
            <p className="my-2 text-xl font-bold">{'차량 위치'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차를 빌린 사용자가 차량을 픽업할 위치를 입력해주세요.'}</h6>
            <div id="carLocation">
              <div className="flex justify-start">
                <div className="flex w-3/4 items-center justify-between rounded-3xl bg-white px-3 py-1">
                  <input
                    className="w-full rounded-2xl p-3 text-xl placeholder:text-background-200 focus:outline-none disabled:bg-white"
                    name="Location"
                    type="text"
                    placeholder="서울시"
                    disabled
                  />
                  <input type="image" src="/search-button.png" width={48} height={48} />
                </div>
              </div>
            </div>
          </div>
          <div>
            <p className="mb-2 mt-4 text-xl font-bold">{'대여료'}</p>
            <h6 className="my-1 text-sm text-background-400">{'사용자에게 시간 당 얼마를 받을지 요금을 입력해주세요.'}</h6>
            <div id="carFee">
              <div className="flex w-3/4 items-center justify-between rounded-3xl bg-white px-3 py-1">
                <input
                  className="w-full p-3 text-xl placeholder:text-background-200 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none"
                  name="Location"
                  type="number"
                  placeholder="100,000"
                  onKeyDown={validateNumber}
                  ref={feeRef}
                />
                <p className="text-bold min-w-16 text-lg text-background-400">{'원/ 시간'}</p>
              </div>
            </div>
          </div>
          <div>
            <p className="mb-2 mt-4 text-xl font-semibold">{'사진 등록'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차량 정면, 후면, 운전석 쪽, 보조석 쪽, 내부 사진 등을 올려주세요.'}</h6>
            <div id="carImages" className="flex min-w-96 max-w-96 flex-row">
              <div className="mr-4 h-72 w-72">
                <ImageUploadButton imageInputRef={imageInputRefs[0]} isLargeButton={true} />
              </div>
              <div className="flex min-w-80 flex-wrap gap-x-4 gap-y-8 max-lg:hidden">
                <ImageUploadButton imageInputRef={imageInputRefs[1]} />
                <ImageUploadButton imageInputRef={imageInputRefs[2]} />
                <ImageUploadButton imageInputRef={imageInputRefs[3]} />
                <ImageUploadButton imageInputRef={imageInputRefs[4]} />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="mt-8 flex justify-end">
        <button className="w-36 rounded-2xl bg-primary-500 px-6 py-3 text-white" onClick={onSubmitRequestCarRegister}>
          {'등록하기'}
        </button>
      </div>
    </>
  );

  return (
    <div className="mx-auto flex min-w-96 flex-col justify-center bg-background-100 align-middle">
      {isCarNumberConfirmed ? HostRegisterForm : CarNumberForm}
    </div>
  );
}

export default HostRegister;

