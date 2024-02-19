package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.dto.SearchCarsResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CarCustomRepository {

    Slice<SearchCarsResultDto> search(SearchCarsDto searchCarsDto, Pageable pageable);

}
