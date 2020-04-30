package com.giggagit.cart.Service;

import java.util.List;

import com.giggagit.cart.Model.Item;
import com.giggagit.cart.Model.Product;

/**
 * ItemService
 */
public interface ItemService {

    public void save(Item item);

    public List<Item> findByProductUpc(long upc);

    public void updateProduct(Product product);

    public void deleteProduct(long upc);

}