//package com.hexacore.tayo.car;
//
//import com.hexacore.tayo.category.CategoryRepository;
//import com.hexacore.tayo.category.SubCategoryRepository;
//import com.hexacore.tayo.category.dto.GetSubCategoryListResponseDto;
//import com.hexacore.tayo.category.model.SubCategory;
//import com.hexacore.tayo.common.response.DataResponseDto;
//import java.util.List;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//
//@ExtendWith(MockitoExtension.class)
//public class CarServiceGetCategoriesTest {
//
//    @Mock
//    private CategoryRepository categoryRepository;
//    @Mock
//    private SubCategoryRepository subCategoryRepository;
//    @InjectMocks
//    private CarService carService;
//
//    @Test
//    @DisplayName("getCategories(): Model 테이블에 존재하는 모든 모델, 세부모델명을 조회한다.")
//    void getCategoriesTest() {
//        // given
//        BDDMockito.given(subCategoryRepository.findAll())
//                .willReturn(List.of(SubCategory.builder().name("세부모델명").build()));
//        // when
//        DataResponseDto response = (DataResponseDto) carService.getSubCategories();
//
//        // then
//        Assertions.assertThat(response.getSuccess()).isTrue();
//        Assertions.assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
//        Assertions.assertThat(response.getData()).isInstanceOf(GetSubCategoryListResponseDto.class);
//        Assertions.assertThat(((GetSubCategoryListResponseDto) response.getData()).getModels().get(0).getName())
//                .isEqualTo("세부모델명");
//    }
//}
