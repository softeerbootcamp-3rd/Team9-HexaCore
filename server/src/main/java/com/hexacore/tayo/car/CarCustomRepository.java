package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.dto.SearchCarsResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarCustomRepository {

    Page<SearchCarsResultDto> search(SearchCarsDto searchCarsDto, Pageable pageable);

}
