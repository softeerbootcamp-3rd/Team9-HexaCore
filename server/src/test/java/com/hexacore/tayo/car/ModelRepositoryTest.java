package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.ModelEntity;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ModelRepositoryTest {

    @Autowired
    private ModelRepository modelRepository;

    @Test
    @DisplayName("subCategory로 검색해서 해당하는 ModelEntity가 있는 경우 Optional<ModelEntity>를 반환하고 없으면 Optional.empty() 객체를 반환한다.")
    void findBySubCategoryTest() {
        // given
        String category = "모델명";
        String subCategory = "모델명 세부모델명";
        ModelEntity model = new ModelEntity();
        model.setCategory(category);
        model.setSubCategory(subCategory);
        modelRepository.save(model);

        // when
        Optional<ModelEntity> existResult = modelRepository.findBySubCategory(subCategory);
        Optional<ModelEntity> noExistResult = modelRepository.findBySubCategory("세부모델명");

        // then
        Assertions.assertThat(existResult.isPresent()).isTrue();
        Assertions.assertThat(existResult.get().getCategory()).isEqualTo(category);
        Assertions.assertThat(existResult.get().getSubCategory()).isEqualTo(subCategory);

        Assertions.assertThat(noExistResult.isEmpty()).isTrue();
    }
}
