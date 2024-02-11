package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CategoryListDto;
import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.common.DataResponseDto;
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
public class CarServiceGetCategoriesTest {

    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private CarService carService;

    @Test
    @DisplayName("getCategories(): Model 테이블에 존재하는 모든 모델, 세부모델명을 조회한다.")
    void getCategoriesTest() {
        // given
        BDDMockito.given(modelRepository.findAll())
                .willReturn(List.of(ModelEntity.builder().category("모델명").subCategory("모델명 세부모델명").build()));
        // when
        DataResponseDto response = (DataResponseDto) carService.getCategories();

        // then
        Assertions.assertThat(response.getSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo(200);
        Assertions.assertThat(response.getData()).isInstanceOf(CategoryListDto.class);
        Assertions.assertThat(((CategoryListDto) response.getData()).getModels().get(0).getCategory()).isEqualTo("모델명");
        Assertions.assertThat(((CategoryListDto) response.getData()).getModels().get(0).getSubCategory())
                .isEqualTo("모델명 세부모델명");
    }
}
