import { ReservationData } from "@/fetches/reservations/Reservation.type";
import Button from "../Button";
import { dateTimeFormatter } from "@/utils/converters";
import { useRef, useState } from "react";
import { fetchCreateReview } from "@/fetches/reviews/fetchReviews";
import { ReviewData } from "@/fetches/reviews/Review.type";
import { useCustomToast } from "../Toast";
import StarRating from "./StarRating";
import { TargetType } from "../ListComponent";

type ReviewModalProps = {
	type: TargetType;
	reservation: ReservationData;
  onClose: () => void;
	finished: () => void;
};

function ReviewModal({ type, reservation, onClose, finished }: ReviewModalProps) {
	const textInput = useRef<HTMLTextAreaElement | null>(null);
	const [rate, setRate] = useState(0);
	const { ToastComponent, showToast } = useCustomToast();

	const onClick = async () => {
		const reviewData : ReviewData = {
			reservationId: reservation.id,
			contents: textInput.current?.value ?? '',
			rate: rate
		};

		const response = await fetchCreateReview(reviewData, type);

		if(!response || !response.success) {
			showToast('리뷰 작성 실패', response?.message || '다시 한 번 시도해주세요');
		} else {
			finished();
			onClose();
		}
	}

  const BackDrop = () => {
    return <div className='fixed left-0 top-0 z-20 h-full w-full bg-background-400 bg-opacity-40'></div>;
  };

  return (
    <>
      <BackDrop />
      <figure className='fixed bottom-0 left-0 right-0 top-0 z-20 flex items-center justify-center'>
        <div className='relative flex min-h-[300px] min-w-[600px] flex-col rounded-2xl border border-background-200 bg-white p-8 shadow-lg'>
          {/* Review Modal Header */}
          <div className='absolute right-0 top-0 flex h-12 w-full items-center justify-end p-3'>
            <button
              onClick={onClose}
              type='button'
              className='ms-auto inline-flex aspect-square w-10 items-center justify-center rounded-lg bg-transparent p-1 text-sm text-background-400'
              data-modal-hide='default-modal'>
              <svg className='h-3 w-3' aria-hidden='true' xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 14 14'>
                <path stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6' />
              </svg>
              <span className='sr-only'>Close modal</span>
            </button>
          </div>

					{/* Review Modal Contents */}
					<div className='flex flex-col gap-5 mt-2'>
						<div className='flex flex-col items-center'>
							<p className='text-xl font-semibold'>{type === 'guest' ? '차량' : '게스트'} 리뷰 남기기</p>
							<p className='text-background-400'>{type === 'guest' ? '사용한 차량' : '게스트'}에 대한 평가를 남겨주세요</p>
						</div>
						<div className='flex justify-between items-center'>
							<div className='flex gap-3'>
								<img
									className='w-14 h-14 rounded-full border border-background-300'
									src={reservation.target.image ?? '/defaultProfile.png'} />
								<div className='flex flex-col justify-center'>
									<p className='font-semibold text-base'>{reservation.target.name}</p>
									<p className='text-sm text-background-500'>{`${dateTimeFormatter(reservation.rentPeriod[0])} 
                      ~ ${dateTimeFormatter(reservation.rentPeriod[1])}`}</p>
								</div>
							</div>
							<div className=''>
								<StarRating rate={rate} onClick={setRate} />
							</div>
						</div>
						<div className='flex flex-col gap-3'>
							<p className="text-sm font-medium"></p>
							<textarea 
								ref={textInput}
								placeholder="리뷰를 작성해주세요."
								className='bg-background-100 rounded-md p-1.5 h-20 align-text-top text-sm
								text-background-800 shadow-sm placeholder:text-background-400 focus:outline-none'/>
						</div>
						<div className='flex justify-center'>
							<Button className='w-40' text="리뷰 작성 완료" onClick={onClick}/>
						</div>
					</div>
        </div>
				<ToastComponent />
      </figure>
    </>
  );
}

export default ReviewModal;
