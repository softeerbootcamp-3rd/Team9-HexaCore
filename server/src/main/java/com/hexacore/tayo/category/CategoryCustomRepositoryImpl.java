package com.hexacore.tayo.category;

import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.QCategory;
import com.hexacore.tayo.category.model.QSubcategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Category> findAllFetch() {
        QCategory category = QCategory.category;
        QSubcategory subcategory = QSubcategory.subcategory;

        return queryFactory
                .selectFrom(category)
                .leftJoin(category.subcategories, subcategory)
                .fetchJoin()
                .fetch();
    }
}
