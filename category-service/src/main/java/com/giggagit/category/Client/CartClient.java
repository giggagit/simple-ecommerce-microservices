package com.giggagit.category.Client;

import com.giggagit.category.Model.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * CartClient
 */
@FeignClient("cart-service")
public interface CartClient {

    @PutMapping("/carts/products")
    public void putItem(@RequestBody Product product);

    @DeleteMapping("/carts/products")
    public void deleteItem(@RequestParam long upc);

}