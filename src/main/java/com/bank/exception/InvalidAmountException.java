package com.bank.exception;

/**
 * Thrown when a transaction amount is invalid (negative, zero, or exceeds limits).
 */
public class InvalidAmountException extends BankingException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
