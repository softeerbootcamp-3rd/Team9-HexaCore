package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long>, JpaSpecificationExecutor<CarEntity> {

    Optional<CarEntity> findByOwner_IdAndCarNumberAndIsDeletedTrue(Long ownerId, String carNumber);

    List<CarEntity> findByOwner_IdAndIsDeletedFalse(Long ownerId);

    List<CarEntity> findByCarNumberAndIsDeletedFalse(String carNumber);
}
