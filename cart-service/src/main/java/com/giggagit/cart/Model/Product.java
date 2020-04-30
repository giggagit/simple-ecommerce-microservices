package com.giggagit.cart.Model;

import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;

/**
 * Product
 */
@Embeddable
public class Product {

    private long upc;
    private String name;

    @Digits(fraction = 2, integer = 8)
    private BigDecimal unitPrice;

    public long getUpc() {
        return upc;
    }

    public void setUpc(long upc) {
        this.upc = upc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (upc ^ (upc >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (upc != other.upc)
            return false;
        return true;
    }

}