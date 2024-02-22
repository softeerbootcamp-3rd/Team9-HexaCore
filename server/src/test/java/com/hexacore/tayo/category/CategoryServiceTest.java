package com.hexacore.tayo.category;

import com.hexacore.tayo.category.dto.GetCategoriesResponseDto;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("getCategories(): Model 테이블에 존재하는 모든 모델, 세부모델명을 조회한다.")
    void getCategoriesTest() {
        // given
        BDDMockito.given(categoryRepository.findAllFetch())
                .willReturn(List.of(Category.builder().name("모델명")
                        .subcategories(List.of(Subcategory.builder().name("모델명 세부모델명").build())).build()));
        // when
        List<GetCategoriesResponseDto.CategoryDto> response = categoryService.getSubcategories();

        // then
        Assertions.assertThat(response.get(0).getName()).isEqualTo("모델명");
        Assertions.assertThat(response.get(0).getSubcategories().get(0).getName()).isEqualTo("모델명 세부모델명");
    }
}
