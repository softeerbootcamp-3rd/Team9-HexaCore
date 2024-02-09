package com.hexacore.tayo.car.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListDto {

    private List<CategoryDto> models;
}
