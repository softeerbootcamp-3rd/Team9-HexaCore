package com.hexacore.tayo.review;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.ReservationRepository;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.review.dto.CreateReviewRequestDto;
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

    @Transactional
    public void createReview(Long writerId, CreateReviewRequestDto createReviewRequestDto,
            boolean isGuestReview) {
        Reservation reservation = reservationRepository.findById(createReviewRequestDto.getReservationId())
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        User user = isGuestReview ? reservation.getGuest() : reservation.getHost();
        ReservationStatus status = reservation.getStatus();

        // 예약과 관련한 유저가 리뷰 작성자와 일치하지 않을 경우
        if (!user.getId().equals(writerId)) {
            throw new GeneralException(ErrorCode.RESERVATION_REVIEWED_BY_OTHERS);
        }

        // 게스트일 경우 예약 상태가 ('TERMINATED' or 'HOST_REVIEW') 가 아닐 경우 리뷰 작성 불가
        // 호스트일 경우 예약 상태가 ('TERMINATED' or 'GUEST_REVIEW') 가 아닐 경우 리뷰 작성 불가
        if (!status.equals(ReservationStatus.TERMINATED)
                && !status.equals(isGuestReview ? ReservationStatus.HOST_REVIEW : ReservationStatus.GUEST_REVIEW)) {
            throw new GeneralException(ErrorCode.CANNOT_ADD_REVIEW);
        }

        Review review = Review.builder()
                .writer(User.builder().id(writerId).build())
                .contents(createReviewRequestDto.getContents())
                .rate(createReviewRequestDto.getRate())
                .build();

        if (isGuestReview) {
            Car car = reservation.getCar();
            // 게스트의 리뷰일 경우 리뷰에 차량 정보를 FK로 연결
            review.setCar(car);

            // 차량 테이블의 average_rate 정보 업데이트
            updateAvgRate(car);
        } else {
            // 호스트의 리뷰일 경우 리뷰에 게스트 정보를 FK로 연결
            review.setGuest(reservation.getGuest());
        }

        reviewRepository.save(review);

        if (status.equals(ReservationStatus.TERMINATED)) {
            reservation.setStatus(isGuestReview ? ReservationStatus.GUEST_REVIEW : ReservationStatus.HOST_REVIEW);
        } else {
            reservation.setStatus(ReservationStatus.REVIEWED);
        }
    }

    private void updateAvgRate(Car car) {
        Double updatedRate = reviewRepository.findAverageRateByCarId(car);
        car.setAverageRate(updatedRate);
    }
}
