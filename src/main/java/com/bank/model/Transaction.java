package com.bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a banking transaction with timestamp and details.
 */
public class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST, FEE
    }

    public Transaction(String transactionId, TransactionType type, double amount, 
                       double balanceAfter, String description) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("| %-12s | %-12s | %10.2f | %12.2f | %-20s | %s |",
                transactionId,
                type,
                amount,
                balanceAfter,
                timestamp.format(formatter),
                description);
    }

    public static String getTableHeader() {
        return String.format("| %-12s | %-12s | %10s | %12s | %-20s | %s |",
                "TXN ID", "TYPE", "AMOUNT", "BALANCE", "TIMESTAMP", "DESCRIPTION") +
                "\n" + "-".repeat(100);
    }
}
