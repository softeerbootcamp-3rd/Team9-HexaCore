package com.hexacore.tayo.review.model;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarReviewRepository extends JpaRepository<CarReview, Long> {

    @Query("SELECT avg (r.rate) FROM CarReview r where r.car = :car")
    Double findAverageRateByCarId(@Param("car") Car car);

    Boolean existsByReservation(Reservation reservation);
}
