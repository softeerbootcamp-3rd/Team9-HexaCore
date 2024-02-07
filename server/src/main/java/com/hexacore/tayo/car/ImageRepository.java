package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.ImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    List<ImageEntity> findByCar_Id(Long carId);
}
