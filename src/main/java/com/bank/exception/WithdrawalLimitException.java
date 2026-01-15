package com.bank.exception;

/**
 * Thrown when withdrawal limit has been reached (for savings accounts).
 */
public class WithdrawalLimitException extends BankingException {
    public WithdrawalLimitException(String message) {
        super(message);
    }
}
