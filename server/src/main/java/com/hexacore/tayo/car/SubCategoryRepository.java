package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.Category;
import com.hexacore.tayo.car.model.SubCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    Optional<SubCategory> findByName(String Name);
}
