import { useState } from 'react';

function HostRegister() {
  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formData = new FormData(e.currentTarget);

    if (formData.get('REGINUMBER') != '') {
      setRegisterNumberConfirmed(true);
    } else {
      alert('등록번호를 잘못 입력하셨습니다!');
    }
  };

  const ImageFormButton = (isLarge: boolean) => {
    const btnClassName: string = isLarge
      ? 'flex h-52 w-52 cursor-pointer items-center justify-center rounded-2xl bg-white p-3'
      : 'flex h-24 w-24 cursor-pointer items-center justify-center rounded-2xl bg-white p-3';
    const imgClassName: string = isLarge ? 'w-12 h-12' : 'w-6 h-6';
    return (
      <>
        <input type="file" accept="image/png, image/jpeg" className="hidden" />
        <div className={btnClassName}>
          <img src={'/form-image-add.png'} alt="Preview" className={imgClassName} />
        </div>
      </>
    );
  };

  const [isRegisterNumberConfirmed, setRegisterNumberConfirmed] = useState<boolean>(false);

  const RegisterNumberForm = (
    <>
      <h1 className="my-7 text-center text-5xl font-bold">Hello</h1>
      <div className="my-5">
        <h4 className="text-center text-background-400">{'스마트한 내차 빌려주기, 시작해볼까요?'}</h4>
        <h4 className="text-center text-background-400">{'정확한 차량번호를 입력해주세요.'}</h4>
      </div>
      <form method="POST" action="https://datahub-dev.scraping.co.kr/assist/common/carzen/CarAllInfoInquiry" onSubmit={onSubmit}>
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
        <div className="mr-12 min-h-80 w-4/12 min-w-40">
          <div id="carInfoSection">
            <p className="my-2 text-xl font-bold">{'차량 정보'}</p>
            <div id="carInfo" className="flex min-h-9 flex-col rounded-lg bg-white p-5">
              <p className="text-background-400">{'제네시스 G80 12가 3456'}</p>
              <p className="text-background-400">{'대형차 | 5인승 | 연료 | 연비'}</p>
            </div>
          </div>
          <div id="additionalInfoSection">
            <p className="mb-2 mt-4 text-xl font-bold">{'부가 설명'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차에 대한 부가 정보나, 차를 사용할 때 주의점을 적어주세요.'}</h6>
            <div id="additionalInfo" className="h-96 min-h-40 rounded-lg bg-white p-4">
              <textarea
                name="carAdditionalInfo"
                placeholder="차량을 소중히 운전해주세요."
                className="h-full min-h-60 w-full resize-none placeholder:text-background-200 focus:outline-none"></textarea>
            </div>
          </div>
        </div>
        <div className="w-6/12 min-w-80">
          <div>
            <p className="my-2 text-xl font-bold">{'차량 위치'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차를 빌린 사용자가 차량을 픽업할 위치를 입력해주세요.'}</h6>
            <div id="carLocation">
              <div className="flex justify-start">
                <div className="flex w-3/4 items-center justify-between rounded-3xl bg-white px-3 py-1">
                  <input className="w-full p-3 text-xl placeholder:text-background-200 focus:outline-none" name="Location" type="text" placeholder="서울시" />
                  <input type="image" src="/search-button.png" width={48} height={48} />
                </div>
              </div>
            </div>
          </div>
          <div>
            <p className="mb-2 mt-4 text-xl font-bold">{'대여료'}</p>
            <h6 className="my-1 text-sm text-background-400">{'사용자에게 하루에 얼마를 받을지 요금을 입력해주세요.'}</h6>
            <div id="carFee">
              <div className="flex w-3/4 items-center justify-between rounded-3xl bg-white px-3 py-1">
                <input
                  className="w-full p-3 text-xl placeholder:text-background-200 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none"
                  name="Location"
                  type="number"
                  placeholder="100,000"
                />
                <p className="text-bold min-w-12 text-lg">{'원/ 일'}</p>
              </div>
            </div>
          </div>
          <div>
            <p className="mb-2 mt-4 text-xl font-bold">{'사진 등록'}</p>
            <h6 className="my-1 text-sm text-background-400">{'차량 정면, 후면, 운전석 쪽, 보조석 쪽, 내부 사진 등을 올려주세요.'}</h6>
            <div id="carImages" className="flex min-w-96 flex-row">
              <div className="mr-4 h-52 w-52">{ImageFormButton(true)}</div>
              <div className="flex min-w-52 max-w-52 flex-wrap gap-4">
                {ImageFormButton(false)}
                {ImageFormButton(false)}
                {ImageFormButton(false)}
                {ImageFormButton(false)}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="flex justify-end">
        <button className="mx-12 rounded-2xl bg-primary-500 px-6 py-3 text-white">{'등록하기'}</button>
      </div>
    </>
  );

  return (
    <div className="mx-auto flex min-w-96 flex-col justify-center bg-background-100 align-middle">
      {isRegisterNumberConfirmed ? HostRegisterForm : RegisterNumberForm}
    </div>
  );
}

export default HostRegister;

