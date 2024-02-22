package com.hexacore.tayo.category;

import com.hexacore.tayo.TestConfig;
import com.hexacore.tayo.category.model.Category;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindAllFetch() {
        // when
        List<Category> categories = categoryRepository.findAllFetch();

        // then
        Assertions.assertThat(categories).isNotNull();
        Assertions.assertThat(categories).hasSizeGreaterThan(0); // 데이터가 적어도 하나 이상 존재한다고 가정
        for (Category category : categories) {
            Assertions.assertThat(category.getSubcategories()).isNotNull(); // 각 Category에 대해 Subcategories가 로드되었는지 확인
        }
    }
}
