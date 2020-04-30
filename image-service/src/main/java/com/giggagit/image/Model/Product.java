package com.giggagit.image.Model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Product
 */
@Embeddable
public class Product {

    @NotNull
    private Long upc;

    public Long getUpc() {
        return upc;
    }

    public void setUpc(Long upc) {
        this.upc = upc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((upc == null) ? 0 : upc.hashCode());
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
        if (upc == null) {
            if (other.upc != null)
                return false;
        } else if (!upc.equals(other.upc))
            return false;
        return true;
    }

}