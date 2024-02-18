package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarCustomRepository {

    Page<Car> search(SearchCarsDto searchCarsDto, Pageable pageable);

}
