package com.hexacore.tayo.car;

import java.util.List;
import java.util.Optional;

import com.hexacore.tayo.car.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByCar_Id(Long carId);

    List<Image> findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(Long carId);

    Optional<Image> findByCar_IdAndOrderIdxAndIsDeletedFalse(Long carId, Integer orderIdx);

    boolean existsByCar_Id(Long carId);

}
