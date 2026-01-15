package com.bank.exception;

/**
 * Thrown when an account cannot be found.
 */
public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
