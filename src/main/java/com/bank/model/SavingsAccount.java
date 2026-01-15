package com.bank.model;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.WithdrawalLimitException;

/**
 * Savings Account with interest earning and withdrawal limits.
 * Demonstrates: Inheritance, Polymorphism, Method Overriding
 */
public class SavingsAccount extends Account {
    private static final double DEFAULT_INTEREST_RATE = 0.025; // 2.5% annual
    private static final double MINIMUM_BALANCE = 100.0;
    private static final int MAX_WITHDRAWALS_PER_MONTH = 6;
    
    private double interestRate;
    private int withdrawalsThisMonth;
    private double accumulatedInterest;

    public SavingsAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, accountHolder, initialBalance);
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.withdrawalsThisMonth = 0;
        this.accumulatedInterest = 0;
    }

    public SavingsAccount(String accountNumber, String accountHolder, 
                          double initialBalance, double interestRate) {
        super(accountNumber, accountHolder, initialBalance);
        this.interestRate = interestRate;
        this.withdrawalsThisMonth = 0;
        this.accumulatedInterest = 0;
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public boolean canWithdraw(double amount) {
        return (balance - amount) >= MINIMUM_BALANCE && withdrawalsThisMonth < MAX_WITHDRAWALS_PER_MONTH;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        
        if (withdrawalsThisMonth >= MAX_WITHDRAWALS_PER_MONTH) {
            throw new WithdrawalLimitException(
                String.format("Monthly withdrawal limit reached (%d/%d). Try again next month.",
                    withdrawalsThisMonth, MAX_WITHDRAWALS_PER_MONTH)
            );
        }
        
        if ((balance - amount) < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(
                String.format("Withdrawal would bring balance below minimum ($%.2f). Available: $%.2f",
                    MINIMUM_BALANCE, balance - MINIMUM_BALANCE)
            );
        }
        
        balance -= amount;
        withdrawalsThisMonth++;
        recordTransaction(Transaction.TransactionType.WITHDRAWAL, amount, 
            String.format("Withdrawal (%d/%d this month)", withdrawalsThisMonth, MAX_WITHDRAWALS_PER_MONTH));
    }

    @Override
    public void applyInterest() {
        double monthlyRate = interestRate / 12;
        double interest = balance * monthlyRate;
        
        if (interest > 0) {
            balance += interest;
            accumulatedInterest += interest;
            recordTransaction(Transaction.TransactionType.INTEREST, interest,
                String.format("Monthly interest @ %.2f%%", interestRate * 100));
        }
    }

    @Override
    public double getAvailableBalance() {
        return Math.max(0, balance - MINIMUM_BALANCE);
    }

    // Savings-specific methods
    public void setInterestRate(double rate) {
        if (rate < 0 || rate > 0.20) {
            throw new IllegalArgumentException("Interest rate must be between 0% and 20%");
        }
        this.interestRate = rate;
    }

    public int getRemainingWithdrawals() {
        return MAX_WITHDRAWALS_PER_MONTH - withdrawalsThisMonth;
    }

    public void resetMonthlyWithdrawals() {
        this.withdrawalsThisMonth = 0;
    }

    public double getAccumulatedInterest() {
        return accumulatedInterest;
    }

    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder(super.getDetailedInfo());
        sb.insert(sb.lastIndexOf("═"), String.format(
            "  Minimum Balance: $%.2f%n  Withdrawals This Month: %d/%d%n  Accumulated Interest: $%.2f%n",
            MINIMUM_BALANCE, withdrawalsThisMonth, MAX_WITHDRAWALS_PER_MONTH, accumulatedInterest));
        return sb.toString();
    }
}
