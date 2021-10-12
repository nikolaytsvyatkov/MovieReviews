package com.fmi.reviews.exception;

public class UnautorizedRequestException extends RuntimeException {
    public UnautorizedRequestException() {
    }

    public UnautorizedRequestException(String message) {
        super(message);
    }

    public UnautorizedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnautorizedRequestException(Throwable cause) {
        super(cause);
    }
}
