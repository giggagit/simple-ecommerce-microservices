package com.giggagit.cart.Repository;

import java.util.List;

import com.giggagit.cart.Model.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ItemRepository
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    public List<Item> findByProductUpc(long upc);

    public void deleteByProductUpc(long upc);

}