

function PaymentIsPending() {
  return (
    <div className='ali flex flex-col justify-center gap-4'>
      <div className='mb-2 flex justify-center text-xl font-bold'>결제 진행 중...</div>
      <div className='flex justify-center'>이 화면을 벗어나지 마세요!</div>
      <div className='flex justify-center'>
        <div className='flex h-20 w-20 animate-spin justify-center rounded-full border-8 border-background-200 border-t-primary-400' />
      </div>
    </div>
  );
}

export default PaymentIsPending;

