package com.hexacore.tayo.car;

import com.hexacore.tayo.category.CategoryRepository;
import com.hexacore.tayo.category.SubCategoryRepository;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.SubCategory;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Test
    @DisplayName("subCategory로 검색해서 해당하는 ModelEntity가 있는 경우 Optional<ModelEntity>를 반환하고 없으면 Optional.empty() 객체를 반환한다.")
    void findBySubCategoryTest() {
        // given
        String category = "모델명";
        String subCategory = "모델명 세부모델명";
        Category model = new Category();
        SubCategory subModel = new SubCategory();
        model.setName(category);
        model.setName(subCategory);
        categoryRepository.save(model);

        // when
        Optional<Category> existResult = categoryRepository.findByName(subCategory);
        Optional<SubCategory> noExistResult = subCategoryRepository.findByName("세부모델명");

        // then
        Assertions.assertThat(existResult).isPresent();
        Assertions.assertThat(existResult.get().getName()).isEqualTo(category);
        Assertions.assertThat(existResult.get().getName()).isEqualTo(subCategory);

        Assertions.assertThat(noExistResult).isEmpty();
    }
}
