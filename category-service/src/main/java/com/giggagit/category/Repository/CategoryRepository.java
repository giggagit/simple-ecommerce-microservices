package com.giggagit.category.Repository;

import com.giggagit.category.Model.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CategoryRepositoty
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public boolean existsByName(String name);

}