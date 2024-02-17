package com.hexacore.tayo.category;

import com.hexacore.tayo.category.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    @EntityGraph(attributePaths = {"subcategories"}, type = EntityGraphType.FETCH)
    @Query("select c from Category c left join fetch c.subcategories")
    List<Category> findAllFetch();
}
