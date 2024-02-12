package com.hexacore.tayo.car;

import java.util.List;
import java.util.Optional;

import com.hexacore.tayo.car.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    List<ImageEntity> findByCar_Id(Long carId);

    List<ImageEntity> findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(Long carId);

    Optional<ImageEntity> findByCar_IdAndOrderIdxAndIsDeletedFalse(Long carId, Integer orderIdx);

    boolean existsByCar_Id(Long carId);

}
