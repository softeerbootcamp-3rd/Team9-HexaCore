package com.hexacore.tayo.category;

import com.hexacore.tayo.category.model.Category;
import java.util.List;

public interface CategoryCustomRepository {

    List<Category> findAllFetch();
}
