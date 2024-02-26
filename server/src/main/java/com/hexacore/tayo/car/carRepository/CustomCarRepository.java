package com.hexacore.tayo.car.carRepository;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.dto.SearchCarsResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomCarRepository {

    Slice<SearchCarsResultDto> search(SearchCarsDto searchCarsDto, Pageable pageable);

}
