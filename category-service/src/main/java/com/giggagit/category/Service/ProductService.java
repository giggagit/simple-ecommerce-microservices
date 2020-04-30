package com.giggagit.category.Service;

import java.util.Optional;

import com.giggagit.category.Model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ProductService
 */
public interface ProductService {

    public Product save(Product product);

    public void deleteById(long upc);

    public Optional<Product> findById(long upc);

    public Page<Product> findAll(Pageable pageable);

    public boolean isProduct(long upc);

}