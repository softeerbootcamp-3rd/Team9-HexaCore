package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarDateRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarDateRangeRepository extends JpaRepository<CarDateRange, Long> {

    void deleteAllByCar_Id(Long CarId);
}
