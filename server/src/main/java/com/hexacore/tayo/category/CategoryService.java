package com.hexacore.tayo.category;

import com.hexacore.tayo.category.dto.GetSubCategoryListResponseDto;
import com.hexacore.tayo.category.dto.GetSubCategoryResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SubCategoryRepository subCategoryRepository;

    /* 모델, 세부 모델명 조회 */
    public GetSubCategoryListResponseDto getSubCategories() {
        List<GetSubCategoryResponseDto> models = subCategoryRepository.findAll().stream()
                .map(subCategory -> new GetSubCategoryResponseDto(subCategory.getName()))
                .toList();
        return new GetSubCategoryListResponseDto(models);
    }
}
