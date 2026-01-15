package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

/**
 * Checking Account with overdraft protection.
 * Demonstrates: Inheritance, Polymorphism, Method Overriding
 */
public class CheckingAccount extends Account {
    private static final double DEFAULT_OVERDRAFT_LIMIT = 500.0;
    private static final double OVERDRAFT_FEE = 35.0;
    private static final double INTEREST_RATE = 0.001; // 0.1% - minimal interest
    
    private double overdraftLimit;
    private double currentOverdraft;
    private int overdraftUsageCount;
    private double totalOverdraftFees;

    public CheckingAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, accountHolder, initialBalance);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
        this.currentOverdraft = 0;
        this.overdraftUsageCount = 0;
        this.totalOverdraftFees = 0;
    }

    public CheckingAccount(String accountNumber, String accountHolder, 
                           double initialBalance, double overdraftLimit) {
        super(accountNumber, accountHolder, initialBalance);
        this.overdraftLimit = overdraftLimit;
        this.currentOverdraft = 0;
        this.overdraftUsageCount = 0;
        this.totalOverdraftFees = 0;
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }

    @Override
    public boolean canWithdraw(double amount) {
        return amount <= getAvailableBalance();
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        double availableWithOverdraft = balance + (overdraftLimit - currentOverdraft);
        
        if (amount > availableWithOverdraft) {
            throw new InsufficientFundsException(
                String.format("Cannot withdraw $%.2f. Available (incl. overdraft): $%.2f",
                    amount, availableWithOverdraft)
            );
        }
        
        // Check if we need to use overdraft
        if (amount > balance) {
            double overdraftNeeded = amount - balance;
            boolean wasInOverdraft = currentOverdraft > 0;
            
            currentOverdraft += overdraftNeeded;
            balance = 0;
            
            // Apply overdraft fee only when entering overdraft (not when already in it)
            if (!wasInOverdraft) {
                applyOverdraftFee();
                overdraftUsageCount++;
            }
            
            recordTransaction(Transaction.TransactionType.WITHDRAWAL, amount,
                String.format("Withdrawal (used $%.2f overdraft)", overdraftNeeded));
        } else {
            balance -= amount;
            recordTransaction(Transaction.TransactionType.WITHDRAWAL, amount, "Withdrawal");
        }
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        
        // First, pay off any overdraft
        if (currentOverdraft > 0) {
            if (amount >= currentOverdraft) {
                double remaining = amount - currentOverdraft;
                recordTransaction(Transaction.TransactionType.DEPOSIT, currentOverdraft,
                    "Overdraft repayment");
                currentOverdraft = 0;
                
                if (remaining > 0) {
                    balance += remaining;
                    recordTransaction(Transaction.TransactionType.DEPOSIT, remaining, "Deposit");
                }
            } else {
                currentOverdraft -= amount;
                recordTransaction(Transaction.TransactionType.DEPOSIT, amount,
                    String.format("Partial overdraft repayment ($%.2f remaining)", currentOverdraft));
            }
        } else {
            balance += amount;
            recordTransaction(Transaction.TransactionType.DEPOSIT, amount, "Deposit");
        }
    }

    @Override
    public void applyInterest() {
        // Checking accounts earn minimal interest only on positive balance
        if (balance > 0 && currentOverdraft == 0) {
            double monthlyRate = INTEREST_RATE / 12;
            double interest = balance * monthlyRate;
            
            if (interest >= 0.01) { // Only apply if at least 1 cent
                balance += interest;
                recordTransaction(Transaction.TransactionType.INTEREST, interest,
                    String.format("Monthly interest @ %.2f%%", INTEREST_RATE * 100));
            }
        }
    }

    private void applyOverdraftFee() {
        totalOverdraftFees += OVERDRAFT_FEE;
        currentOverdraft += OVERDRAFT_FEE;
        recordTransaction(Transaction.TransactionType.FEE, OVERDRAFT_FEE, "Overdraft fee");
    }

    @Override
    public double getBalance() {
        return balance - currentOverdraft;
    }

    @Override
    public double getAvailableBalance() {
        return balance + (overdraftLimit - currentOverdraft);
    }

    // Checking-specific methods
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double limit) {
        if (limit < 0 || limit > 10000) {
            throw new IllegalArgumentException("Overdraft limit must be between $0 and $10,000");
        }
        this.overdraftLimit = limit;
    }

    public double getCurrentOverdraft() {
        return currentOverdraft;
    }

    public double getRemainingOverdraft() {
        return overdraftLimit - currentOverdraft;
    }

    public int getOverdraftUsageCount() {
        return overdraftUsageCount;
    }

    public double getTotalOverdraftFees() {
        return totalOverdraftFees;
    }

    public boolean isInOverdraft() {
        return currentOverdraft > 0;
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(50)).append("\n");
        sb.append(String.format("  Account Type: %s%n", getAccountType()));
        sb.append(String.format("  Account Number: %s%n", accountNumber));
        sb.append(String.format("  Account Holder: %s%n", accountHolder));
        sb.append(String.format("  Current Balance: $%.2f%n", getBalance()));
        sb.append(String.format("  Available Balance: $%.2f%n", getAvailableBalance()));
        sb.append(String.format("  Interest Rate: %.2f%%%n", getInterestRate() * 100));
        sb.append(String.format("  Overdraft Limit: $%.2f%n", overdraftLimit));
        sb.append(String.format("  Current Overdraft: $%.2f%n", currentOverdraft));
        sb.append(String.format("  Overdraft Status: %s%n", isInOverdraft() ? "⚠ IN OVERDRAFT" : "✓ Clear"));
        sb.append(String.format("  Total Overdraft Fees: $%.2f%n", totalOverdraftFees));
        sb.append(String.format("  Total Transactions: %d%n", transactionHistory.size()));
        sb.append("═".repeat(50));
        return sb.toString();
    }
}
