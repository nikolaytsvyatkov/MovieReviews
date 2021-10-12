package com.fmi.reviews.exception;

public class UnexistingEntityException extends RuntimeException{
    public UnexistingEntityException() {
        super();
    }

    public UnexistingEntityException(String message) {
        super(message);
    }

    public UnexistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexistingEntityException(Throwable cause) {
        super(cause);
    }
}
