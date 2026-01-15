package com.bank.service;

import com.bank.exception.*;
import com.bank.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Central service for all banking operations.
 * Manages accounts and handles transfers between them.
 */
public class BankService {
    private final Map<String, Account> accounts;
    private final AtomicInteger accountNumberGenerator;
    private final String bankName;

    public BankService(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
        this.accountNumberGenerator = new AtomicInteger(1000);
    }

    // Account Creation - Factory Pattern
    public SavingsAccount createSavingsAccount(String holderName, double initialDeposit) {
        String accountNumber = generateAccountNumber("SAV");
        SavingsAccount account = new SavingsAccount(accountNumber, holderName, initialDeposit);
        accounts.put(accountNumber, account);
        return account;
    }

    public SavingsAccount createSavingsAccount(String holderName, double initialDeposit, double interestRate) {
        String accountNumber = generateAccountNumber("SAV");
        SavingsAccount account = new SavingsAccount(accountNumber, holderName, initialDeposit, interestRate);
        accounts.put(accountNumber, account);
        return account;
    }

    public CheckingAccount createCheckingAccount(String holderName, double initialDeposit) {
        String accountNumber = generateAccountNumber("CHK");
        CheckingAccount account = new CheckingAccount(accountNumber, holderName, initialDeposit);
        accounts.put(accountNumber, account);
        return account;
    }

    public CheckingAccount createCheckingAccount(String holderName, double initialDeposit, double overdraftLimit) {
        String accountNumber = generateAccountNumber("CHK");
        CheckingAccount account = new CheckingAccount(accountNumber, holderName, initialDeposit, overdraftLimit);
        accounts.put(accountNumber, account);
        return account;
    }

    private String generateAccountNumber(String prefix) {
        return prefix + "-" + accountNumberGenerator.incrementAndGet();
    }

    // Account Retrieval
    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public Optional<Account> findAccount(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> getAccountsByHolder(String holderName) {
        return accounts.values().stream()
                .filter(a -> a.getAccountHolder().equalsIgnoreCase(holderName))
                .collect(Collectors.toList());
    }

    public List<SavingsAccount> getSavingsAccounts() {
        return accounts.values().stream()
                .filter(a -> a instanceof SavingsAccount)
                .map(a -> (SavingsAccount) a)
                .collect(Collectors.toList());
    }

    public List<CheckingAccount> getCheckingAccounts() {
        return accounts.values().stream()
                .filter(a -> a instanceof CheckingAccount)
                .map(a -> (CheckingAccount) a)
                .collect(Collectors.toList());
    }

    // Core Banking Operations
    public void deposit(String accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        account.deposit(amount);
    }

    public void withdraw(String accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        account.withdraw(amount);
    }

    /**
     * Transfer funds between accounts.
     * Demonstrates polymorphism - works with any Account type.
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new TransferException("Cannot transfer to the same account");
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        // Validate the transfer can proceed
        if (!fromAccount.canWithdraw(amount)) {
            throw new TransferException(
                String.format("Insufficient funds for transfer. Available: $%.2f, Requested: $%.2f",
                    fromAccount.getAvailableBalance(), amount)
            );
        }

        try {
            // Perform the transfer
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            // Update transaction descriptions
            updateLastTransactionDescription(fromAccount, 
                String.format("Transfer to %s", toAccountNumber));
            updateLastTransactionDescription(toAccount, 
                String.format("Transfer from %s", fromAccountNumber));

        } catch (Exception e) {
            throw new TransferException("Transfer failed: " + e.getMessage(), e);
        }
    }

    private void updateLastTransactionDescription(Account account, String description) {
        // This is a simplification - in real systems, transactions would be immutable
        List<Transaction> history = account.getTransactionHistory();
        if (!history.isEmpty()) {
            Transaction last = history.get(history.size() - 1);
            // We record the transfer info in subsequent transactions
        }
    }

    // Interest Operations
    public void applyInterestToAllAccounts() {
        accounts.values().forEach(Account::applyInterest);
    }

    public void applyInterestToSavingsAccounts() {
        getSavingsAccounts().forEach(SavingsAccount::applyInterest);
    }

    public double calculateTotalInterestEarned() {
        return getSavingsAccounts().stream()
                .mapToDouble(SavingsAccount::getAccumulatedInterest)
                .sum();
    }

    // Monthly Maintenance
    public void performMonthlyMaintenance() {
        // Apply interest
        applyInterestToAllAccounts();
        
        // Reset savings withdrawal counters
        getSavingsAccounts().forEach(SavingsAccount::resetMonthlyWithdrawals);
    }

    // Reporting
    public double getTotalDeposits() {
        return accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public int getTotalAccountCount() {
        return accounts.size();
    }

    public Map<String, Double> getAccountBalanceSummary() {
        Map<String, Double> summary = new LinkedHashMap<>();
        summary.put("Total Savings", getSavingsAccounts().stream()
                .mapToDouble(Account::getBalance).sum());
        summary.put("Total Checking", getCheckingAccounts().stream()
                .mapToDouble(Account::getBalance).sum());
        summary.put("Total Overdraft Used", getCheckingAccounts().stream()
                .mapToDouble(CheckingAccount::getCurrentOverdraft).sum());
        summary.put("Grand Total", getTotalDeposits());
        return summary;
    }

    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    public void closeAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account.getBalance() != 0) {
            throw new BankingException(
                String.format("Cannot close account with non-zero balance: $%.2f", account.getBalance())
            );
        }
        accounts.remove(accountNumber);
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  %-60s║%n", bankName + " - Summary Report"));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Total Accounts: %-43d║%n", getTotalAccountCount()));
        sb.append(String.format("║  Savings Accounts: %-41d║%n", getSavingsAccounts().size()));
        sb.append(String.format("║  Checking Accounts: %-40d║%n", getCheckingAccounts().size()));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        
        Map<String, Double> summary = getAccountBalanceSummary();
        for (Map.Entry<String, Double> entry : summary.entrySet()) {
            sb.append(String.format("║  %-30s $%,18.2f     ║%n", entry.getKey() + ":", entry.getValue()));
        }
        
        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
}
