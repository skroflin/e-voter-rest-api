package com.skroflin.evoting_rest_api.exceptions.user;

public class EmailDeliveryFailedException extends RuntimeException {
    public EmailDeliveryFailedException(String message) {
        super(message);
    }

    public EmailDeliveryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
