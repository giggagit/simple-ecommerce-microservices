package com.giggagit.category.Service;

import java.util.Optional;

import com.giggagit.category.Model.Category;
import com.giggagit.category.Repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * CategoryServiceImpl
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepositoty;

    public CategoryServiceImpl(CategoryRepository categoryRepositoty) {
        this.categoryRepositoty = categoryRepositoty;
    }

    @Override
    public Category save(Category category) {
        return categoryRepositoty.save(category);
    }

    @Override
    public void deleteById(long categoryId) {
        categoryRepositoty.deleteById(categoryId);
    }

    @Override
    public Optional<Category> findById(long categoryId) {
        Optional<Category> category = categoryRepositoty.findById(categoryId);
        return category;
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepositoty.findAll(pageable);
    }

    @Override
    public boolean isCategory(String name) {
        return categoryRepositoty.existsByName(name);
    }

}