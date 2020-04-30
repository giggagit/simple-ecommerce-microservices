package com.giggagit.category.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.giggagit.category.Model.Product;
import com.giggagit.category.Service.ProductService;

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
 * ProductController
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findAll(pageable);
        return ResponseEntity.ok(productPage);
    }

    @PostMapping
    public ResponseEntity<?> postProduct(@Validated @RequestBody Product product) {
        if (productService.isProduct(product.getUpc())) {
            return ResponseEntity.badRequest().build();
        }

        Product productData = productService.save(product);
        URI location = null;

        try {
            location = new URI("/products/" + productData.getUpc());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{upc}")
    public ResponseEntity<Product> getProductUpc(@PathVariable long upc) {
        Optional<Product> productOptional = productService.findById(upc);

        if (!productOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productOptional.get());
    }

    @PutMapping("/{upc}")
    public ResponseEntity<Product> putProductUpc(@PathVariable long upc, @Validated @RequestBody Product product) {
        Optional<Product> productOptional = productService.findById(upc);

        if (!productOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product productGet = productOptional.get();

        productGet.setName(product.getName());
        productGet.setDescription(product.getDescription());
        productGet.setCategory(product.getCategory());
        productGet.setUnitPrice(product.getUnitPrice());
        productGet.setDateModified(LocalDateTime.now());

        product = productService.save(productGet);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{upc}")
    public ResponseEntity<?> deleteProductUpc(@PathVariable long upc) {
        Optional<Product> productOptional = productService.findById(upc);

        if (!productOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteById(upc);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/isproduct/{upc}")
    public ResponseEntity<Boolean> isProduct(@PathVariable long upc) {
        return ResponseEntity.ok(productService.isProduct(upc));
    }

}