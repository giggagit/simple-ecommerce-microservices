package com.giggagit.category.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ProductException
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ProductException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }

}