package com.hexacore.tayo.category;

import com.hexacore.tayo.category.dto.GetCategoriesResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /* 모델, 세부 모델명 조회 */
    public List<GetCategoriesResponseDto.CategoryDto> getSubcategories() {
        return categoryRepository.findAllFetch().stream()
                .map(GetCategoriesResponseDto.CategoryDto::new)
                .toList();
    }
}
