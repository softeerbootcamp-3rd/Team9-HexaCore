package com.hexacore.tayo.car;

import java.util.ArrayList;
import java.util.List;
import com.hexacore.tayo.car.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository <ImageEntity, Long> {
    List<ImageEntity> findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(Long carId);
}
