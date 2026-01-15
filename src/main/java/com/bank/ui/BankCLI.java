package com.bank.ui;

import com.bank.exception.*;
import com.bank.model.*;
import com.bank.service.BankService;
import java.util.List;
import java.util.Scanner;

/**
 * Command-Line Interface for the Bank Management System.
 * Provides interactive menu for all banking operations.
 */
public class BankCLI {
    private final BankService bankService;
    private final Scanner scanner;
    private boolean running;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BOLD = "\u001B[1m";

    public BankCLI(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        printWelcome();
        
        while (running) {
            printMainMenu();
            int choice = getIntInput("Enter your choice: ");
            processMainMenuChoice(choice);
        }
        
        printGoodbye();
        scanner.close();
    }

    private void printWelcome() {
        System.out.println("\n" + ANSI_CYAN + ANSI_BOLD);
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║       ██████╗  █████╗ ███╗   ██╗██╗  ██╗                     ║");
        System.out.println("║       ██╔══██╗██╔══██╗████╗  ██║██║ ██╔╝                     ║");
        System.out.println("║       ██████╔╝███████║██╔██╗ ██║█████╔╝                      ║");
        System.out.println("║       ██╔══██╗██╔══██║██║╚██╗██║██╔═██╗                      ║");
        System.out.println("║       ██████╔╝██║  ██║██║ ╚████║██║  ██╗                     ║");
        System.out.println("║       ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝                     ║");
        System.out.println("║                                                               ║");
        System.out.println("║              MANAGEMENT SYSTEM v1.0                           ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println(ANSI_RESET);
        System.out.println("  Welcome to " + bankService.getBankName() + "!");
        System.out.println("  Your trusted partner in financial management.\n");
    }

    private void printMainMenu() {
        System.out.println("\n" + ANSI_YELLOW + "═══════════════════ MAIN MENU ══════════════════" + ANSI_RESET);
        System.out.println("  1. " + ANSI_GREEN + "Create Account" + ANSI_RESET);
        System.out.println("  2. " + ANSI_GREEN + "View Account Details" + ANSI_RESET);
        System.out.println("  3. " + ANSI_GREEN + "Deposit Funds" + ANSI_RESET);
        System.out.println("  4. " + ANSI_GREEN + "Withdraw Funds" + ANSI_RESET);
        System.out.println("  5. " + ANSI_GREEN + "Transfer Funds" + ANSI_RESET);
        System.out.println("  6. " + ANSI_GREEN + "View Transaction History" + ANSI_RESET);
        System.out.println("  7. " + ANSI_GREEN + "Apply Monthly Interest" + ANSI_RESET);
        System.out.println("  8. " + ANSI_GREEN + "List All Accounts" + ANSI_RESET);
        System.out.println("  9. " + ANSI_GREEN + "Bank Summary Report" + ANSI_RESET);
        System.out.println("  0. " + ANSI_RED + "Exit" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "════════════════════════════════════════════════" + ANSI_RESET);
    }

    private void processMainMenuChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> viewAccountDetails();
                case 3 -> depositFunds();
                case 4 -> withdrawFunds();
                case 5 -> transferFunds();
                case 6 -> viewTransactionHistory();
                case 7 -> applyMonthlyInterest();
                case 8 -> listAllAccounts();
                case 9 -> showBankSummary();
                case 0 -> running = false;
                default -> printError("Invalid choice. Please try again.");
            }
        } catch (BankingException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void createAccount() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ CREATE NEW ACCOUNT ══════════" + ANSI_RESET);
        System.out.println("  1. Savings Account");
        System.out.println("  2. Checking Account");
        System.out.println("  0. Back to Main Menu");
        
        int type = getIntInput("Select account type: ");
        
        if (type == 0) return;
        if (type != 1 && type != 2) {
            printError("Invalid account type");
            return;
        }
        
        String holderName = getStringInput("Enter account holder name: ");
        double initialDeposit = getDoubleInput("Enter initial deposit amount: $");
        
        Account account;
        if (type == 1) {
            account = bankService.createSavingsAccount(holderName, initialDeposit);
            printSuccess("Savings account created successfully!");
        } else {
            double overdraftLimit = getDoubleInput("Enter overdraft limit (default 500): $");
            if (overdraftLimit <= 0) overdraftLimit = 500;
            account = bankService.createCheckingAccount(holderName, initialDeposit, overdraftLimit);
            printSuccess("Checking account created successfully!");
        }
        
        System.out.println("\n" + account.getDetailedInfo());
    }

    private void viewAccountDetails() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ ACCOUNT DETAILS ══════════" + ANSI_RESET);
        String accountNumber = getStringInput("Enter account number: ");
        
        Account account = bankService.getAccount(accountNumber);
        System.out.println("\n" + account.getDetailedInfo());
    }

    private void depositFunds() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ DEPOSIT FUNDS ══════════" + ANSI_RESET);
        String accountNumber = getStringInput("Enter account number: ");
        double amount = getDoubleInput("Enter deposit amount: $");
        
        bankService.deposit(accountNumber, amount);
        
        Account account = bankService.getAccount(accountNumber);
        printSuccess(String.format("Successfully deposited $%.2f", amount));
        System.out.println(String.format("  New balance: $%.2f", account.getBalance()));
    }

    private void withdrawFunds() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ WITHDRAW FUNDS ══════════" + ANSI_RESET);
        String accountNumber = getStringInput("Enter account number: ");
        
        Account account = bankService.getAccount(accountNumber);
        System.out.println(String.format("  Available balance: $%.2f", account.getAvailableBalance()));
        
        double amount = getDoubleInput("Enter withdrawal amount: $");
        
        bankService.withdraw(accountNumber, amount);
        
        account = bankService.getAccount(accountNumber);
        printSuccess(String.format("Successfully withdrew $%.2f", amount));
        System.out.println(String.format("  New balance: $%.2f", account.getBalance()));
    }

    private void transferFunds() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ TRANSFER FUNDS ══════════" + ANSI_RESET);
        String fromAccount = getStringInput("Enter source account number: ");
        
        Account source = bankService.getAccount(fromAccount);
        System.out.println(String.format("  Available balance: $%.2f", source.getAvailableBalance()));
        
        String toAccount = getStringInput("Enter destination account number: ");
        double amount = getDoubleInput("Enter transfer amount: $");
        
        bankService.transfer(fromAccount, toAccount, amount);
        
        printSuccess(String.format("Successfully transferred $%.2f from %s to %s", 
                amount, fromAccount, toAccount));
        
        System.out.println(String.format("  Source account new balance: $%.2f", 
                bankService.getAccount(fromAccount).getBalance()));
        System.out.println(String.format("  Destination account new balance: $%.2f", 
                bankService.getAccount(toAccount).getBalance()));
    }

    private void viewTransactionHistory() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ TRANSACTION HISTORY ══════════" + ANSI_RESET);
        String accountNumber = getStringInput("Enter account number: ");
        
        Account account = bankService.getAccount(accountNumber);
        List<Transaction> transactions = account.getTransactionHistory();
        
        System.out.println("\n" + ANSI_BOLD + "Transaction History for " + accountNumber + ANSI_RESET);
        System.out.println(String.format("Account Holder: %s | Current Balance: $%.2f\n",
                account.getAccountHolder(), account.getBalance()));
        
        if (transactions.isEmpty()) {
            System.out.println("  No transactions found.");
        } else {
            System.out.println(Transaction.getTableHeader());
            for (Transaction t : transactions) {
                System.out.println(t);
            }
            System.out.println("-".repeat(100));
            System.out.println(String.format("Total transactions: %d", transactions.size()));
        }
    }

    private void applyMonthlyInterest() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ APPLY MONTHLY INTEREST ══════════" + ANSI_RESET);
        System.out.println("This will apply interest to all accounts and reset monthly limits.");
        String confirm = getStringInput("Proceed? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            bankService.performMonthlyMaintenance();
            printSuccess("Monthly maintenance completed!");
            System.out.println("  - Interest applied to all accounts");
            System.out.println("  - Savings withdrawal counters reset");
        } else {
            System.out.println("  Operation cancelled.");
        }
    }

    private void listAllAccounts() {
        System.out.println("\n" + ANSI_CYAN + "═══════════ ALL ACCOUNTS ══════════" + ANSI_RESET);
        List<Account> accounts = bankService.getAllAccounts();
        
        if (accounts.isEmpty()) {
            System.out.println("  No accounts found.");
            return;
        }
        
        System.out.println("\n" + ANSI_BOLD + "SAVINGS ACCOUNTS:" + ANSI_RESET);
        System.out.println("-".repeat(80));
        List<SavingsAccount> savingsAccounts = bankService.getSavingsAccounts();
        if (savingsAccounts.isEmpty()) {
            System.out.println("  None");
        } else {
            System.out.printf("  %-15s %-25s %15s %15s%n", "ACCOUNT #", "HOLDER", "BALANCE", "AVAILABLE");
            System.out.println("-".repeat(80));
            for (SavingsAccount acc : savingsAccounts) {
                System.out.printf("  %-15s %-25s $%,14.2f $%,14.2f%n",
                        acc.getAccountNumber(), acc.getAccountHolder(), 
                        acc.getBalance(), acc.getAvailableBalance());
            }
        }
        
        System.out.println("\n" + ANSI_BOLD + "CHECKING ACCOUNTS:" + ANSI_RESET);
        System.out.println("-".repeat(80));
        List<CheckingAccount> checkingAccounts = bankService.getCheckingAccounts();
        if (checkingAccounts.isEmpty()) {
            System.out.println("  None");
        } else {
            System.out.printf("  %-15s %-25s %15s %15s %10s%n", 
                    "ACCOUNT #", "HOLDER", "BALANCE", "AVAILABLE", "OVERDRAFT");
            System.out.println("-".repeat(80));
            for (CheckingAccount acc : checkingAccounts) {
                String overdraftStatus = acc.isInOverdraft() ? 
                        String.format("$%.2f", acc.getCurrentOverdraft()) : "Clear";
                System.out.printf("  %-15s %-25s $%,14.2f $%,14.2f %10s%n",
                        acc.getAccountNumber(), acc.getAccountHolder(),
                        acc.getBalance(), acc.getAvailableBalance(), overdraftStatus);
            }
        }
        
        System.out.println("-".repeat(80));
        System.out.printf("  Total Accounts: %d%n", accounts.size());
    }

    private void showBankSummary() {
        System.out.println(bankService.getBankSummary());
    }

    // Input Helpers
    private int getIntInput(String prompt) {
        System.out.print(ANSI_YELLOW + prompt + ANSI_RESET);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print(ANSI_RED + "Invalid input. " + ANSI_YELLOW + prompt + ANSI_RESET);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private double getDoubleInput(String prompt) {
        System.out.print(ANSI_YELLOW + prompt + ANSI_RESET);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print(ANSI_RED + "Invalid input. " + ANSI_YELLOW + prompt + ANSI_RESET);
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }

    private String getStringInput(String prompt) {
        System.out.print(ANSI_YELLOW + prompt + ANSI_RESET);
        return scanner.nextLine().trim();
    }

    private void printSuccess(String message) {
        System.out.println("\n" + ANSI_GREEN + "✓ " + message + ANSI_RESET);
    }

    private void printError(String message) {
        System.out.println("\n" + ANSI_RED + "✗ Error: " + message + ANSI_RESET);
    }

    private void printGoodbye() {
        System.out.println("\n" + ANSI_CYAN);
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║   Thank you for using " + bankService.getBankName() + "!                     ║");
        System.out.println("║   Have a great day!                                           ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println(ANSI_RESET);
    }
}
