package com.hexacore.tayo.car;

import java.util.List;
import java.util.Optional;

import com.hexacore.tayo.car.model.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {

    List<CarImage> findByCar_Id(Long carId);

    Optional<CarImage> findByCar_IdAndOrderIdx(Long carId, Integer orderIdx);

    boolean existsByCar_Id(Long carId);

}
