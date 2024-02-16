package com.hexacore.tayo.category.dto;

import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GetCategoriesResponseDto {

    @Getter
    @AllArgsConstructor
    public static class CategoryDto {

        private Long id;
        private String name;
        private List<SubcategoryDto> subcategories;

        public CategoryDto(Category category) {
            this.id = category.getId();
            this.name = category.getName();
            this.subcategories = category.getSubcategories().stream().map(SubcategoryDto::new).toList();
        }
    }
    @Getter
    @AllArgsConstructor
    public static class SubcategoryDto {

        private Long id;
        private String name;

        public SubcategoryDto(Subcategory subcategory) {
            this.id = subcategory.getId();
            this.name = subcategory.getName();
        }
    }
}

