package com.hexacore.tayo.review;

import com.hexacore.tayo.car.carRepository.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.reservation.ReservationRepository;
import com.hexacore.tayo.review.dto.CreateReviewRequestDto;
import com.hexacore.tayo.review.dto.GetCarReviewsResponseDto;
import com.hexacore.tayo.review.model.CarReview;
import com.hexacore.tayo.review.model.CarReviewRepository;
import com.hexacore.tayo.review.model.GuestReview;
import com.hexacore.tayo.review.model.GuestReviewRepository;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CarReviewRepository carReviewRepository;
    private final GuestReviewRepository guestReviewRepository;
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    @Transactional
    public void createReview(Long writerId, CreateReviewRequestDto createReviewRequestDto, boolean isGuest) {
        Reservation reservation = reservationRepository.findById(createReviewRequestDto.getReservationId())
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        // 리뷰 작성자 확인
        User writer = isGuest ? reservation.getGuest() : reservation.getHost();
        if (!writer.getId().equals(writerId)) {
            throw new GeneralException(ErrorCode.RESERVATION_REVIEWED_BY_OTHERS);
        }

        // 예약 상태 확인
        if (!reservation.getStatus().equals(ReservationStatus.TERMINATED)) {
            throw new GeneralException(ErrorCode.CANNOT_ADD_REVIEW);
        }

        // 리뷰 존재 여부 확인
        if (isReviewed(reservation, isGuest)) {
            throw new GeneralException(ErrorCode.REVIEW_ALREADY_EXIST);
        }

        // 리뷰 작성
        if (isGuest) {
            createCarReview(writerId, reservation, createReviewRequestDto);
        } else {
            createGuestReview(writerId, reservation, createReviewRequestDto);
        }
    }

    @Transactional
    void createCarReview(Long writerId, Reservation reservation,
            CreateReviewRequestDto createReviewRequestDto) {
        Car car = reservation.getCar();

        CarReview review = CarReview.builder()
                .writer(User.builder().id(writerId).build())
                .car(car)
                .reservation(reservation)
                .contents(createReviewRequestDto.getContents())
                .rate(createReviewRequestDto.getRate())
                .build();

        // 리뷰 저장
        carReviewRepository.save(review);

        // 차량 평균 평점 업데이트
        Double updatedRate = carReviewRepository.findAverageRateByCarId(car);
        car.setAverageRate(updatedRate);
    }

    @Transactional
    void createGuestReview(Long writerId, Reservation reservation,
            CreateReviewRequestDto createReviewRequestDto) {
        User guest = reservation.getGuest();

        GuestReview review = GuestReview.builder()
                .writer(User.builder().id(writerId).build())
                .guest(guest)
                .reservation(reservation)
                .contents(createReviewRequestDto.getContents())
                .rate(createReviewRequestDto.getRate())
                .build();

        // 리뷰 저장
        guestReviewRepository.save(review);

        // 게스트 평균 평점 업데이트
        Double updatedRate = guestReviewRepository.findAverageRateByCarId(guest);
        guest.setAverageRate(updatedRate);
    }

    /* 차량 리뷰 조회 */
    public Page<GetCarReviewsResponseDto> getCarReviews(Long carId, Pageable pageable) {
        if (carRepository.findByIdAndIsDeletedFalse(carId).isEmpty()) {
            throw new GeneralException(ErrorCode.CAR_NOT_FOUND);
        }
        return carReviewRepository.findAllByCarId(carId, pageable);
    }

    /* 리뷰 있는지 확인 */
    public boolean isReviewed(Reservation reservation, boolean isGuest) {
        return isGuest ? carReviewRepository.existsByReservation(reservation) :
                guestReviewRepository.existsByReservation(reservation);
    }
}
