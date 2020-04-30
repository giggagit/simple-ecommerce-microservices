package com.giggagit.image.Client;

import com.giggagit.image.Model.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * CategoryClient
 */
@FeignClient(name = "category-service", decode404 = true)
public interface CategoryClient {

    @GetMapping("/products/{upc}")
    public ResponseEntity<Product> getProduct(@PathVariable long upc);

}