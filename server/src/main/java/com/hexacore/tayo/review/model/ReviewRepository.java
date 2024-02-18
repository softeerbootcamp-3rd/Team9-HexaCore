package com.hexacore.tayo.review.model;

import com.hexacore.tayo.car.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT avg (r.rate) FROM Review r where r.car = :car")
    Double findAverageRateByCarId(@Param("car") Car car);
}
