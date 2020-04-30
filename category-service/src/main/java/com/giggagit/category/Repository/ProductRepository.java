package com.giggagit.category.Repository;

import com.giggagit.category.Model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}