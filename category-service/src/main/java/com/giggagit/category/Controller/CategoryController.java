package com.giggagit.category.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.giggagit.category.Model.Category;
import com.giggagit.category.Service.CategoryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CategoryController
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getCategory(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.findAll(pageable);
        return ResponseEntity.ok(categoryPage);
    }

    @PostMapping
    public ResponseEntity<?> postCategory(@Validated @RequestBody Category category) {

        if (categoryService.isCategory(category.getName())) {
            return ResponseEntity.badRequest().build();
        }

        category.setId(null);
        category = categoryService.save(category);
        URI location = null;

        try {
            location = new URI("/category/" + category.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryId(@PathVariable long categoryId) {
        Optional<Category> categoryOptional = categoryService.findById(categoryId);

        if (!categoryOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryOptional.get());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> putCategoryUpc(@PathVariable long categoryId,
            @Validated @RequestBody Category category) {
        Optional<Category> categoryOptional = categoryService.findById(categoryId);

        if (!categoryOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Category categoryGet = categoryOptional.get();

        categoryGet.setName(category.getName());
        categoryGet.setDescription(category.getDescription());

        category = categoryService.save(categoryGet);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable long categoryId) {
        Optional<Category> categoryOptional = categoryService.findById(categoryId);

        if (!categoryOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        categoryService.deleteById(categoryId);
        return ResponseEntity.noContent().build();
    }

}