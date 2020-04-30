package com.giggagit.image.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * StorageException
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class StorageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}