package com.giggagit.cart.Repository;

import com.giggagit.cart.Model.Cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CartRepository
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}