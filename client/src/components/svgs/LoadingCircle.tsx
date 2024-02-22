function LoadingCircle() {
  return (
    <div
      className='border-current inline-block h-5 w-5 animate-spin rounded-full border-4 border-solid border-r-transparent align-[-0.125em] text-white motion-reduce:animate-[spin_1.5s_linear_infinite]'
      role='status'>
      <span className='!absolute !-m-px !h-px !w-px !overflow-hidden !whitespace-nowrap !border-0 !p-0 ![clip:rect(0,0,0,0)]'>Loading...</span>
    </div>
  );
}

export default LoadingCircle;

