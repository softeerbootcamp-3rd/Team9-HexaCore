package com.hexacore.tayo.car.carRepository;

import com.hexacore.tayo.car.model.Car;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, CustomCarRepository {

    Optional<Car> findByIdAndIsDeletedFalse(Long id);

    Optional<Car> findByOwner_IdAndCarNumberAndIsDeletedTrue(Long ownerId, String carNumber);

    Optional<Car> findByOwner_IdAndIsDeletedFalse(Long ownerId);

    List<Car> findByCarNumberAndIsDeletedFalse(String carNumber);
}
