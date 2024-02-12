import Button from './Button';

type Props = {
  target: { type: string; name: string; phoneNumber: string; image: string };
  reservation: { startDate: Date; endDate: Date; status: string; price?: number; address: string };
  className?: string;
};

function ListComponent({ target, reservation, className }: Props) {
  let buttonText;
  let buttonType: 'disabled' | 'danger' | 'enabled' | undefined;

  if (reservation.status === 'cancel') {
    if (target.type === 'host') {
      buttonText = '거절완료';
      buttonType = 'disabled';
    } else {
      buttonText = '예약실패';
      buttonType = 'disabled';
    }
  } else if (reservation.status === 'ready') {
    if (target.type === 'host') {
      buttonText = '거절하기';
      buttonType = 'danger';
    } else {
      buttonText = '대여시작';
      buttonType = 'enabled';
    }
  } else if (reservation.status === 'using') {
    if (target.type === 'host') {
      buttonText = '반납확인';
      buttonType = 'enabled';
    } else {
      buttonText = '대여중';
      buttonType = 'disabled';
    }
  } else {
    if (target.type === 'host') {
      buttonText = '반납완료';
      buttonType = 'disabled';
    } else {
      buttonText = '사용완료';
      buttonType = 'disabled';
    }
  }

  return (
    <div
      className={`
		flex flex-col bg-white rounded-3xl p-4 h-full shadow-md text-sm md:text-base
		${className}`}>
      <ul role="list" className="divide-y divide-gray-100 min-h-20">
        <li key="person.email" className="h-full">
          <div className="h-full w-full flex justify-between items-center">
            <div className="h-full flex items-center min-w-0 gap-x-4">
              <img
                className={`h-12 w-12 flex-none bg-gray-50 ${target.type === 'host' ? 'rounded-full ml-6' : 'min-h-24 min-w-24 rounded-lg'}`}
                src={target.image}
                alt=""
                onError={(e) => {
                  const imgElement = e.target as HTMLImageElement;
                  imgElement.src = '../public/default-profile.png';
                }}
              />
              <div className="flex flex-col justify-between">
                <p className="text-md font-semibold leading-60">{target.name}</p>
                <div>
                  <p className="text-xs leading-5 text-background-500 break-all">{target.phoneNumber}</p>
                  {target.type === 'guest' && (
                    <>
                      <p className="truncate text-xs leading-5 text-background-500">{reservation.address}</p>
                      <p className="text-xs leading-6 text-background-500">
                        {reservation.startDate?.getFullYear()}.{('0' + (reservation.startDate.getMonth() + 1)).slice(-2)}.
                        {('0' + reservation.startDate.getDate()).slice(-2)} ~ {reservation.endDate.getFullYear()}.
                        {('0' + (reservation.endDate?.getMonth() + 1)).slice(-2)}.{('0' + reservation.endDate.getDate()).slice(-2)}
                      </p>
                    </>
                  )}
                </div>
              </div>
            </div>
            <div className={`w-1/2 h-full flex justify-end ml-6 ${target.type == 'host' ? 'items-center' : 'items-end'}`}>
              <div className="w-1/2 mr-6 flex flex-col">
                {target.type === 'host' && (
                  <p className="text-xs text-right leading-6 text-background-500">
                    {reservation.startDate.getFullYear()}.{('0' + (reservation.startDate.getMonth() + 1)).slice(-2)}.
                    {('0' + reservation.startDate.getDate()).slice(-2)} ~ {reservation.endDate.getFullYear()}.
                    {('0' + (reservation.endDate.getMonth() + 1)).slice(-2)}.{('0' + reservation.endDate.getDate()).slice(-2)}
                  </p>
                )}
                <p className={`truncate text-md font-semibold text-right leading-5 ${target.type === 'guest' ? 'mb-2' : ''}`}>
                  {reservation.price || undefined}원
                </p>
              </div>
              <Button className="w-1/4 min-w-14 h-auto mr-6 rounded-xl text-xs lg:text-sm break-all" type={buttonType} text={buttonText}></Button>
            </div>
          </div>
        </li>
      </ul>
    </div>
  );
}

export default ListComponent;
