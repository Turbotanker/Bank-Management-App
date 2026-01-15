package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for all bank accounts.
 * Demonstrates OOP principles: Abstraction, Encapsulation
 */
public abstract class Account {
    protected final String accountNumber;
    protected final String accountHolder;
    protected double balance;
    protected final LocalDateTime createdAt;
    protected final List<Transaction> transactionHistory;
    private int transactionCounter;

    public Account(String accountNumber, String accountHolder, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.createdAt = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
        this.transactionCounter = 0;

        if (initialBalance > 0) {
            recordTransaction(Transaction.TransactionType.DEPOSIT, initialBalance, "Initial deposit");
        }
    }

    // Abstract methods - must be implemented by subclasses (Polymorphism)
    public abstract String getAccountType();
    public abstract double getInterestRate();
    public abstract void applyInterest();
    public abstract boolean canWithdraw(double amount);

    // Template method for withdrawal - uses canWithdraw() polymorphically
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(
                String.format("Cannot withdraw $%.2f. Available: $%.2f", amount, getAvailableBalance())
            );
        }
        
        balance -= amount;
        recordTransaction(Transaction.TransactionType.WITHDRAWAL, amount, "Cash withdrawal");
    }

    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        balance += amount;
        recordTransaction(Transaction.TransactionType.DEPOSIT, amount, "Cash deposit");
    }

    protected void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        if (amount > 1_000_000) {
            throw new InvalidAmountException("Amount exceeds maximum transaction limit of $1,000,000");
        }
    }

    protected String generateTransactionId() {
        return accountNumber.substring(0, 4) + "-" + String.format("%04d", ++transactionCounter);
    }

    protected void recordTransaction(Transaction.TransactionType type, double amount, String description) {
        Transaction transaction = new Transaction(
            generateTransactionId(),
            type,
            amount,
            balance,
            description
        );
        transactionHistory.add(transaction);
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public double getAvailableBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public List<Transaction> getRecentTransactions(int count) {
        int start = Math.max(0, transactionHistory.size() - count);
        return Collections.unmodifiableList(transactionHistory.subList(start, transactionHistory.size()));
    }

    @Override
    public String toString() {
        return String.format("%s Account [%s] - %s | Balance: $%.2f",
                getAccountType(), accountNumber, accountHolder, balance);
    }

    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(50)).append("\n");
        sb.append(String.format("  Account Type: %s%n", getAccountType()));
        sb.append(String.format("  Account Number: %s%n", accountNumber));
        sb.append(String.format("  Account Holder: %s%n", accountHolder));
        sb.append(String.format("  Current Balance: $%.2f%n", balance));
        sb.append(String.format("  Available Balance: $%.2f%n", getAvailableBalance()));
        sb.append(String.format("  Interest Rate: %.2f%%%n", getInterestRate() * 100));
        sb.append(String.format("  Total Transactions: %d%n", transactionHistory.size()));
        sb.append("═".repeat(50));
        return sb.toString();
    }
}
