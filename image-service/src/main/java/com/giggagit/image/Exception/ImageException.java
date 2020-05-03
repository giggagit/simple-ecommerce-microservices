package com.giggagit.image.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ImageException
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ImageException(String message) {
        super(message);
    }

    public ImageException(String message, Throwable cause) {
        super(message, cause);
    }

}