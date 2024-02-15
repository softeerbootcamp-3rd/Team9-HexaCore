package com.hexacore.tayo.category;

import com.hexacore.tayo.category.dto.GetCategoriesResponseDto;
import com.hexacore.tayo.category.dto.GetCategoriesResponseDto.CategoryListDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SubcategoryRepository subcategoryRepository;

    /* 모델, 세부 모델명 조회 */
    public GetCategoriesResponseDto.CategoryListDto getSubCategories() {
        List<GetCategoriesResponseDto.CategoryDto> models = subcategoryRepository.findAll().stream()
                .map(GetCategoriesResponseDto.CategoryDto::new)
                .toList();
        return new CategoryListDto(models);
    }
}
