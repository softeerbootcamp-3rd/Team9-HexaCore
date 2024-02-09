package com.hexacore.tayo.car;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hexacore.tayo.car.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends JpaRepository <ImageEntity, Long> {

    List<ImageEntity> findByCar_Id(Long carId);
    List<ImageEntity> findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(Long carId);

    Optional<ImageEntity> findByCar_IdAndOrderIdxAndIsDeletedFalse(Long carId,Integer orderIdx);

}
