package com.hexacore.tayo.car.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryDto {

    private String category;
    private String subCategory;

    public CategoryDto(ModelEntity model) {
        this.category = model.getCategory();
        this.subCategory = model.getSubCategory();
    }
}
