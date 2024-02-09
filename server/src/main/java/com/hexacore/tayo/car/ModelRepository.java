package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.ModelEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, Long> {

    Optional<ModelEntity> findBySubCategory(String subCategory);
}
