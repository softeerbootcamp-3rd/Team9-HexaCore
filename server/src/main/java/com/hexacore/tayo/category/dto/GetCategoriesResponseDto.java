package com.hexacore.tayo.category.dto;

import com.hexacore.tayo.category.model.Subcategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GetCategoriesResponseDto {

    @Getter
    @AllArgsConstructor
    public static class CategoryListDto {

        private List<CategoryDto> models;
    }

    @Getter
    @AllArgsConstructor
    public static class CategoryDto {

        private String category;
        private String subcategory;

        public CategoryDto(Subcategory subcategory) {
            this.category = subcategory.getCategory().getName();
            this.subcategory = subcategory.getName();
        }
    }
}

