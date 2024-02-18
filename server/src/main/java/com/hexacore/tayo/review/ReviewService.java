package com.hexacore.tayo.review;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.ReservationRepository;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.review.dto.CreateReviewForCarRequestDto;
import com.hexacore.tayo.review.model.Review;
import com.hexacore.tayo.review.model.ReviewRepository;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /* 차량에 대한 리뷰 등록 */
    @Transactional
    public void createReviewForCar(Long writerId, CreateReviewForCarRequestDto createReviewForCarRequestDto) {
        Reservation reservation = reservationRepository.findById(createReviewForCarRequestDto.getReservationId())
                // 예약 정보가 존재하지 않을 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약의 게스트와 현재 로그인한 유저가 일치하지 않을 경우
        if (!reservation.getGuest().getId().equals(writerId)) {
            throw new GeneralException(ErrorCode.RESERVATION_REVIEWED_BY_OTHERS);
        }

        // 예약 상태가 'TERMINATED' 가 아닌 경우 (CANCEL, READY, USING, REVIEWED) 를 퉁쳐서 "리뷰를 달 수 없습니다." 에러메시지 던짐
        if (!reservation.getStatus().equals(ReservationStatus.TERMINATED)) {
            throw new GeneralException(ErrorCode.CANNOT_ADD_REVIEW);
        }

        Car car = reservation.getCar();

        Review review = Review.builder()
                .writer(User.builder().id(writerId).build())
                .car(car)
                .contents(createReviewForCarRequestDto.getContents())
                .rate(createReviewForCarRequestDto.getRate())
                .build();

        // 리뷰 정보 저장
        reviewRepository.save(review);

        // 차량 테이블의 average_rate 정보 업데이트
        updateAvgRate(car);

        // 예약 상태 REVIEWED 로 수정
        reservation.setStatus(ReservationStatus.REVIEWED);
    }

    private void updateAvgRate(Car car) {
        Double updatedRate = reviewRepository.findAverageRateByCarId(car);
        car.setAverageRate(updatedRate);
    }
}
