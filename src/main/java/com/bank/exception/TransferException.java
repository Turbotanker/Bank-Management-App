package com.bank.exception;

/**
 * Thrown when a transfer operation fails.
 */
public class TransferException extends BankingException {
    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
