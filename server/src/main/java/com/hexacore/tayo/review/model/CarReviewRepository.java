package com.hexacore.tayo.review.model;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.review.dto.GetCarReviewsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarReviewRepository extends JpaRepository<CarReview, Long> {

    @Query("SELECT avg (r.rate) FROM CarReview r where r.car = :car")
    Double findAverageRateByCarId(@Param("car") Car car);

    Boolean existsByReservation(Reservation reservation);

    @Query(value = "SELECT "
            + "new com.hexacore.tayo.review.dto.GetCarReviewsResponseDto(r.id, u.name, u.profileImgUrl, r.contents, r.rate) "
            + "FROM CarReview r "
            + "JOIN User u ON r.writer.id = u.id "
            + "WHERE r.car.id = :carId")
    Page<GetCarReviewsResponseDto> findAllByCarId(@Param("carId") Long carId, Pageable pageable);
}
