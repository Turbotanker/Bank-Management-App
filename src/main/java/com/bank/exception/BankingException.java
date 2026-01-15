package com.bank.exception;

/**
 * Base exception class for all banking-related exceptions.
 */
public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
