package com.giggagit.cart.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.giggagit.cart.Model.Cart;
import com.giggagit.cart.Model.Product;
import com.giggagit.cart.Service.CartService;
import com.giggagit.cart.Service.ItemService;

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
 * CartController
 */
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;

    public CartController(CartService cartService, ItemService itemService) {
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Cart> postCart() {
        Cart cart = cartService.save(new Cart());
        URI location = null;

        try {
            location = new URI("/carts/" + cart.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable long cartId) {
        Optional<Cart> cart = cartService.findById(cartId);

        if (!cart.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cart.get());
    }

    @PostMapping("/{cartId}")
    public ResponseEntity<Cart> postCartId(@PathVariable long cartId, @RequestParam("item") long upc,
            @RequestParam("quantity") int quantity) {
        Optional<Cart> cart = cartService.findById(cartId);

        if (!cart.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Cart cartData = cartService.addItem(upc, quantity, cart.get());
        return ResponseEntity.ok(cartData);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> putCartId(@PathVariable long cartId, @RequestParam("item") long upc,
            @RequestParam("quantity") int quantity) {
        Optional<Cart> cart = cartService.findById(cartId);

        if (!cart.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Cart cartData = cartService.updateCart(upc, quantity, cart.get());

        return ResponseEntity.ok(cartData);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Cart> deleteCartId(@PathVariable long cartId, @RequestParam("item") long upc) {
        Optional<Cart> cart = cartService.findById(cartId);

        if (!cart.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Cart cartData = cartService.deleteItem(upc, cart.get());
        return ResponseEntity.ok(cartData);
    }

    @DeleteMapping("/{cartId}/test")
    public ResponseEntity<Cart> deleteCartId(@PathVariable long cartId) {
        cartService.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/products")
    public ResponseEntity<?> putProducts(@Validated @RequestBody Product product) {
        itemService.updateProduct(product);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products")
    public ResponseEntity<?> deleteProducts(@RequestParam long upc) {
        itemService.deleteProduct(upc);
        return ResponseEntity.noContent().build();
    }

}