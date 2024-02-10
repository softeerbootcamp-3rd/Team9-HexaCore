import Button from './Button';

type Props = {
  user: { type: string; name: string; phoneNumber: string };
  reservation: { startDate: string; endDate: string; status: string; price: number; address: string };
  className?: string;
};

function ListComponent({ user, reservation, className }: Props) {
  let buttonText;
  let buttonType: 'disabled' | 'danger' | 'enabled' | undefined;
  const startDate = new Date(reservation.startDate);
  const endDate = new Date(reservation.endDate);

  if (reservation.status === 'cancel') {
    if (user.type === 'host') {
      buttonText = '거절완료';
      buttonType = 'disabled';
    } else {
      buttonText = '예약실패';
      buttonType = 'disabled';
    }
  } else if (reservation.status === 'ready') {
    if (user.type === 'host') {
      buttonText = '거절하기';
      buttonType = 'danger';
    } else {
      buttonText = '대여시작';
      buttonType = 'enabled';
    }
  } else if (reservation.status === 'using') {
    if (user.type === 'host') {
      buttonText = '반납확인';
      buttonType = 'disabled';
    } else {
      buttonText = '대여중';
      buttonType = 'enabled';
    }
  } else {
    if (user.type === 'host') {
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
		flex flex-col bg-white rounded-3xl p-4 
		${className}`}>
      <ul role="list" className="divide-y divide-gray-100">
        <li key="person.email">
          <div className="h-full w-full flex justify-between items-center">
            <div className="flex min-w-0 gap-x-4">
              <img className="h-12 w-12 flex-none rounded-full bg-gray-50" src="/default-profile.png" alt="" />
              <div className="min-w-0 flex-auto">
                <p className="text-md font-semibold leading-60">{user.name}</p>
                <p className="truncate text-xs leading-5 text-background-500">{user.phoneNumber}</p>
                {user.type === 'guest' && <p className="truncate text-xs leading-5 text-background-500">{reservation.address}</p>}
              </div>
            </div>
            <div className="w-1/2 flex items-center justify-end">
              <div className="w-1/2 mr-6 flex flex-col">
                <p className="text-xs text-right leading-6 text-background-500">
                  {startDate.getFullYear()}.{startDate.getMonth()}.{startDate.getDate()} ~ {endDate.getFullYear()}.{endDate.getMonth()}.{endDate.getDate()}
                </p>
                <p className="truncate text-md font-semibold text-right leading-5">{reservation.price}원</p>
              </div>
              <Button className="w-1/4 h-2/5 mr-6" type={buttonType} text={buttonText}></Button>
            </div>
          </div>
        </li>
      </ul>
    </div>
  );
}

export default ListComponent;
