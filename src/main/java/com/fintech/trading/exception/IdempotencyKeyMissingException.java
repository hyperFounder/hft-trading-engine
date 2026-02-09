package com.fintech.trading.exception;

/**
 * Thrown when a required idempotency key is missing from the request headers.
 */
public class IdempotencyKeyMissingException extends RuntimeException {
    public IdempotencyKeyMissingException(String message) {
        super(message);
    }
}