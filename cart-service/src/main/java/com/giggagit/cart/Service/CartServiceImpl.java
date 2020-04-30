package com.giggagit.cart.Service;

import java.math.BigDecimal;
import java.util.Optional;

import com.giggagit.cart.Client.CategoryClient;
import com.giggagit.cart.Exception.ProductNotFoundException;
import com.giggagit.cart.Model.Cart;
import com.giggagit.cart.Model.Item;
import com.giggagit.cart.Model.Product;
import com.giggagit.cart.Repository.CartRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CartServiceImpl
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CategoryClient categoryClient;

    public CartServiceImpl(CartRepository cartRepository, CategoryClient categoryClient) {
        this.cartRepository = cartRepository;
        this.categoryClient = categoryClient;
    }

    @Override
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public void deleteById(long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    @Transactional
    public Optional<Cart> findById(long cartId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        cart.ifPresent(c -> c.getItems().size());
        return cart;
    }

    @Override
    @Transactional
    public Cart addItem(long upc, int quantity, Cart cart) {
        ResponseEntity<Product> productResponse = categoryClient.getProduct(upc);

        if (productResponse.getStatusCode().isError()) {
            throw new ProductNotFoundException("Product UPC not found!, UPC: " + upc);
        }

        Optional<Item> itemOptional = cart.getItems().stream().filter(c -> c.getProduct().getUpc() == upc).findFirst();
        Item item = null;

        if (itemOptional.isPresent()) {
            item = itemOptional.get();

            item.setQuantity(item.getQuantity() + quantity);
            item.setSubTotal(BigDecimal.valueOf(item.getQuantity()).multiply(item.getProduct().getUnitPrice()));
        } else {
            item = new Item();

            item.setProduct(productResponse.getBody());
            item.setQuantity(quantity);
            item.setSubTotal(BigDecimal.valueOf(quantity).multiply(productResponse.getBody().getUnitPrice()));
        }

        cart.addItem(item);
        cart = save(updateTotal(cart));
        return cart;
    }

    @Override
    @Transactional
    public Cart updateCart(long upc, int quantity, Cart cart) {
        Optional<Item> itemOptional = cart.getItems().stream().filter(c -> c.getProduct().getUpc() == upc).findFirst();
        Item item = null;

        if (!itemOptional.isPresent()) {
            throw new ProductNotFoundException("Product UPC not found!, UPC: " + upc);
        }

        item = itemOptional.get();
        item.setQuantity(quantity);
        item.setSubTotal(BigDecimal.valueOf(quantity).multiply(item.getProduct().getUnitPrice()));
        cart.addItem(item);
        cart = save(updateTotal(cart));
        return cart;
    }

    @Override
    public Cart deleteItem(long upc, Cart cart) {
        if (cart.getItems().isEmpty()) {
            return cart;
        }

        cart.getItems().removeIf(i -> i.getProduct().getUpc() == upc);
        cart = updateTotal(cart);
        return cart;
    }

    @Override
    public Cart updateTotal(Cart cart) {
        if (cart.getItems().isEmpty()) {
            cart.setTotalPrice(BigDecimal.valueOf(0));
            return cart;
        }

        cart.setTotalPrice(cart.getItems().stream().map(Item::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return cart;
    }

    @Override
    public Cart updateSubTotal(int quantity, Product product) {
        return null;
    }

    @Override
    public boolean isCart(long cartId) {
        return cartRepository.existsById(cartId);
    }

}