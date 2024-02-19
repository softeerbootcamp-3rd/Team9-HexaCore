package com.hexacore.tayo.review.model;

import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestReviewRepository extends JpaRepository<GuestReview, Long> {


    @Query("SELECT avg (r.rate) FROM GuestReview r where r.guest = :guest")
    Double findAverageRateByCarId(@Param("guest") User guest);

    Boolean existsByReservation(Reservation reservation);
}
