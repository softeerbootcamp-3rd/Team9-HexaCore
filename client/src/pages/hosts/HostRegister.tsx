import ImageUploadButton from '@/pages/hosts/ImageUploadButton';
import { KeyboardEvent, WheelEvent, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function HostRegister() {
  const imageInputRefs = Array.from({ length: 5 }, () => useRef<HTMLInputElement>(null));
  const descriptionRef = useRef<HTMLTextAreaElement>(null);
  const addressRef = useRef<HTMLInputElement>(null);
  const feeRef = useRef<HTMLInputElement>(null);
  const feeErrorRef = useRef<HTMLDivElement>(null);
  const [isCarNumberConfirmed, setCarNumberConfirmed] = useState<boolean>(false);
  const navigator = useNavigate();

  const disableWheel = (e: WheelEvent<HTMLInputElement>) => {
    e.currentTarget.blur();
  };

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

    if (descriptionRef.current == null || feeRef.current == null || feeErrorRef.current == null) return;

    if (feeRef.current.validity.rangeUnderflow || feeRef.current.validity.stepMismatch) {
      feeErrorRef.current.hidden = false;
      feeErrorRef.current.innerText = '대여료는 1000원 이상, 단위는 1000원입니다. 다시 입력해주세요.';
      return;
    }

    if (feeRef.current.validity.valueMissing) {
      feeErrorRef.current.hidden = false;
      feeErrorRef.current.innerText = '대여료는 필수로 입력하셔야 합니다.';
      return;
    }

    formData.append('description', descriptionRef.current.value);
    formData.append('feePerHour', feeRef.current.value);

    // TODO: 서버에 차량 등록 요청을 하고 성공/실패 여부에 따라 리다이렉트한다.
    // carNumber, carName, address, position 등의 속성은 외부 API를 사용한다.
    // position 속성은 등록하기 버튼을 누른 후, 위치 정보로 좌표 관련 외부 API를 사용하여 좌표값을 얻는다.
    // 등록에 성공하였다면 호스트 페이지로 이동한다.
    alert('차량 등록을 완료하였습니다.');
    navigator('/hosts/manage');
  };

  const onClickGetLocation = async () => {
    alert('위치 정보를 입력하였습니다.');

    if (addressRef.current) addressRef.current.value = '서울특별시 서초구 헌릉로 12';

    // TODO: 외부 위치 정보 API를 활용해서 위치 정보를 갱신한다.
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
      <div className="flex min-w-full flex-col justify-center">
        <div>
          <div className="flex justify-center">
            <div className="mx-6 flex min-h-80 w-5/12 min-w-40 flex-col justify-between">
              <div id="carInfoSection">
                <p className="my-2 text-xl font-semibold">{'차량 정보'}</p>
                <div id="carInfo" className="flex h-24 flex-col items-start justify-center rounded-3xl bg-white p-5">
                  <p className="text-lg text-background-400">{'제네시스 G80 12가 3456'}</p>
                  <p className="text-lg text-background-400">{'대형차 | 5인승 | 연료 | 연비'}</p>
                </div>
              </div>
              <div id="additionalInfoSection" className="flex grow flex-col pb-1">
                <p className="mb-2 mt-4 text-xl font-semibold">{'부가 설명'}</p>
                <h6 className="my-1 text-sm text-background-400">{'차에 대한 부가 정보나, 차를 사용할 때 주의점을 적어주세요.'}</h6>
                <div id="additionalInfo" className="min-h-40 grow rounded-3xl bg-white p-4">
                  <textarea
                    ref={descriptionRef}
                    name="carAdditionalInfo"
                    placeholder="차량을 소중히 운전해주세요."
                    className="h-full min-h-60 w-full grow resize-none placeholder:text-background-200 focus:outline-none"></textarea>
                </div>
              </div>
            </div>
            <div className="mx-6 flex w-5/12 min-w-80 flex-col justify-between">
              <div>
                <p className="my-2 text-xl font-semibold">{'차량 위치'}</p>
                <h6 className="my-1 text-sm text-background-400">{'차를 빌린 사용자가 차량을 픽업할 위치를 입력해주세요.'}</h6>
                <div id="carLocation">
                  <div className="flex justify-start">
                    <div className="flex w-3/4 cursor-pointer items-center justify-between rounded-3xl bg-white px-3 py-1" onClick={onClickGetLocation}>
                      <input
                        className="w-full cursor-pointer rounded-2xl p-3 text-xl placeholder:text-background-200 focus:outline-none disabled:bg-white"
                        name="Location"
                        type="text"
                        placeholder="서울시"
                        ref={addressRef}
                        readOnly
                      />
                      <input type="image" src="/search-button.png" width={48} height={48} />
                    </div>
                  </div>
                </div>
              </div>
              <div>
                <p className="mb-2 mt-4 text-xl font-semibold">{'대여료'}</p>
                <h6 className="my-1 text-sm text-background-400">{'사용자에게 시간 당 얼마를 받을지 요금을 입력해주세요.'}</h6>
                <div id="carFee">
                  <div className="my-1 text-sm text-danger-400" hidden ref={feeErrorRef}></div>
                  <div className="flex w-3/4 items-center justify-between rounded-3xl bg-white px-3 py-1">
                    <input
                      className="w-full p-3 text-xl placeholder:text-background-200 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none"
                      name="Fee"
                      type="number"
                      placeholder="100,000"
                      onKeyDown={validateNumber}
                      onWheel={disableWheel}
                      step={1000}
                      min={1000}
                      ref={feeRef}
                      required
                    />
                    <p className="text-semibold min-w-16 text-lg text-background-400">{'원/ 시간'}</p>
                  </div>
                </div>
              </div>
              <div className="flex h-96 flex-col">
                <p className="mb-2 mt-4 text-xl font-semibold">{'사진 등록'}</p>
                <h6 className="my-1 text-sm text-background-400">{'차량 정면, 후면, 운전석 쪽, 보조석 쪽, 내부 사진 등을 올려주세요.'}</h6>
                <div id="carImages" className="flex h-4/5 flex-wrap content-start">
                  <div className="h-full w-1/2">
                    <ImageUploadButton imageInputRef={imageInputRefs[0]} isLargeButton={true} />
                  </div>
                  <div className="flex h-full w-1/2 flex-wrap content-start">
                    <ImageUploadButton imageInputRef={imageInputRefs[1]} />
                    <ImageUploadButton imageInputRef={imageInputRefs[2]} />
                    <ImageUploadButton imageInputRef={imageInputRefs[3]} />
                    <ImageUploadButton imageInputRef={imageInputRefs[4]} />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="mt-8 flex justify-center">
            <div className="mx-6 w-9/12 py-3"></div>
            <div className="flex w-1/12 justify-end p-2">
              <button className="min-w-32 rounded-2xl bg-primary-500 px-6 py-3 text-white" onClick={onSubmitRequestCarRegister}>
                {'등록하기'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );

  if (isCarNumberConfirmed) return HostRegisterForm;
  return CarNumberForm;
}

export default HostRegister;

