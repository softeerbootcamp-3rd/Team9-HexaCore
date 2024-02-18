import { useDaumPostcodePopup } from 'react-daum-postcode';

type Props = {
  address: string;
  buttonImgUrl: string;
  className: string;
  setAddress: React.Dispatch<React.SetStateAction<string>>;
};

const AddressButton = ({ address, setAddress, buttonImgUrl, className }: Props) => {
  const openPopup = useDaumPostcodePopup();
  const handleComplete = (data: any) => {
    const roadAddress: string = data.address;
    setAddress(roadAddress);
  };
  const handleOnClick = (e: React.MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    openPopup({ onComplete: handleComplete });
  };

  return (
    <div onClick={handleOnClick} className={className}>
      <input
        className='text-md w-4/5 min-w-32 cursor-pointer rounded-2xl p-3 placeholder:text-background-200 focus:outline-none disabled:bg-white'
        name='Location'
        type='text'
        placeholder='서울특별시 서초구 헌릉로 12'
        value={address}
        readOnly
      />
      <img src={buttonImgUrl} className='h-10 w-10' />
    </div>
  );
};

export default AddressButton;

