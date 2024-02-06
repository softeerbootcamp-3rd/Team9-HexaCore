package com.hexacore.tayo.car.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Setter
@Getter
@AllArgsConstructor
public class DateListDto {

    private List<Pair<Date, Date>> dates;
}
