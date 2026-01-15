package com.bank;

import com.bank.exception.*;
import com.bank.model.*;
import com.bank.service.BankService;

/**
 * Mock tests for the Bank Management System.
 * Demonstrates testing without external test frameworks.sti
 * 
 * Tests cover:
 * - Account creation
 * - Deposits and withdrawals
 * - Transfers between accounts
 * - Interest calculations
 * - Overdraft handling
 * - Exception scenarios
 */
public class BankManagementTest {
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           BANK MANAGEMENT SYSTEM - TEST SUITE                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Run all test categories
        testAccountCreation();
        testSavingsAccountOperations();
        testCheckingAccountOperations();
        testTransferOperations();
        testInterestCalculations();
        testOverdraftHandling();
        testExceptionScenarios();
        testTransactionHistory();

        // Print summary
        printTestSummary();
    }

    // ==================== Account Creation Tests ====================
    private static void testAccountCreation() {
        printTestCategory("Account Creation");

        // Test 1: Create Savings Account
        test("Create Savings Account", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("John Doe", 1000.0);
            
            assertEqual("John Doe", account.getAccountHolder());
            assertEqual(1000.0, account.getBalance());
            assertEqual("Savings", account.getAccountType());
            assertTrue(account.getAccountNumber().startsWith("SAV-"));
        });

        // Test 2: Create Checking Account
        test("Create Checking Account", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Jane Doe", 500.0);
            
            assertEqual("Jane Doe", account.getAccountHolder());
            assertEqual(500.0, account.getBalance());
            assertEqual("Checking", account.getAccountType());
            assertTrue(account.getAccountNumber().startsWith("CHK-"));
        });

        // Test 3: Create Savings Account with Custom Interest Rate
        test("Savings Account with Custom Interest Rate", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Alice", 2000.0, 0.05);
            
            assertEqual(0.05, account.getInterestRate());
        });

        // Test 4: Create Checking Account with Custom Overdraft Limit
        test("Checking Account with Custom Overdraft", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Bob", 1000.0, 1500.0);
            
            assertEqual(1500.0, account.getOverdraftLimit());
        });
    }

    // ==================== Savings Account Operations ====================
    private static void testSavingsAccountOperations() {
        printTestCategory("Savings Account Operations");

        // Test 1: Deposit to Savings
        test("Deposit to Savings Account", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            account.deposit(500.0);
            assertEqual(1500.0, account.getBalance());
        });

        // Test 2: Withdraw from Savings
        test("Withdraw from Savings Account", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            account.withdraw(400.0);
            assertEqual(600.0, account.getBalance());
        });

        // Test 3: Withdrawal counter
        test("Savings Withdrawal Counter", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 5000.0);
            
            assertEqual(6, account.getRemainingWithdrawals());
            account.withdraw(100.0);
            assertEqual(5, account.getRemainingWithdrawals());
        });

        // Test 4: Available balance respects minimum
        test("Savings Available Balance with Minimum", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 500.0);
            
            // Available should be balance minus $100 minimum
            assertEqual(400.0, account.getAvailableBalance());
        });
    }

    // ==================== Checking Account Operations ====================
    private static void testCheckingAccountOperations() {
        printTestCategory("Checking Account Operations");

        // Test 1: Deposit to Checking
        test("Deposit to Checking Account", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 1000.0);
            
            account.deposit(250.0);
            assertEqual(1250.0, account.getBalance());
        });

        // Test 2: Withdraw from Checking
        test("Withdraw from Checking Account", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 1000.0);
            
            account.withdraw(300.0);
            assertEqual(700.0, account.getBalance());
        });

        // Test 3: Available balance includes overdraft
        test("Checking Available Balance with Overdraft", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 1000.0, 500.0);
            
            // Available should be balance plus overdraft limit
            assertEqual(1500.0, account.getAvailableBalance());
        });
    }

    // ==================== Transfer Operations ====================
    private static void testTransferOperations() {
        printTestCategory("Transfer Operations");

        // Test 1: Basic Transfer
        test("Basic Transfer Between Accounts", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount savings = bank.createSavingsAccount("User 1", 1000.0);
            CheckingAccount checking = bank.createCheckingAccount("User 2", 500.0);
            
            bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), 200.0);
            
            assertEqual(800.0, savings.getBalance());
            assertEqual(700.0, checking.getBalance());
        });

        // Test 2: Transfer between same account type
        test("Transfer Between Savings Accounts", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount acc1 = bank.createSavingsAccount("User 1", 2000.0);
            SavingsAccount acc2 = bank.createSavingsAccount("User 2", 1000.0);
            
            bank.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 500.0);
            
            assertEqual(1500.0, acc1.getBalance());
            assertEqual(1500.0, acc2.getBalance());
        });

        // Test 3: Transfer to pay off overdraft
        test("Transfer to Pay Off Overdraft", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount savings = bank.createSavingsAccount("User 1", 1000.0);
            CheckingAccount checking = bank.createCheckingAccount("User 2", 100.0);
            
            // Put checking in overdraft
            checking.withdraw(200.0); // Uses overdraft
            assertTrue(checking.isInOverdraft());
            
            // Transfer to pay off
            bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), 300.0);
            assertFalse(checking.isInOverdraft());
        });
    }

    // ==================== Interest Calculations ====================
    private static void testInterestCalculations() {
        printTestCategory("Interest Calculations");

        // Test 1: Savings Interest Application
        test("Savings Account Interest Application", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 12000.0, 0.12); // 12% annual
            
            account.applyInterest(); // Monthly = 1%
            
            // 12000 * 0.01 = 120 interest
            assertEqual(12120.0, account.getBalance());
            assertEqual(120.0, account.getAccumulatedInterest());
        });

        // Test 2: Checking Account Minimal Interest
        test("Checking Account Interest (Minimal)", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 10000.0);
            
            double beforeBalance = account.getBalance();
            account.applyInterest();
            
            // Checking earns 0.1% annual = ~0.00833% monthly
            assertTrue(account.getBalance() >= beforeBalance);
        });

        // Test 3: Bank-wide Interest Application
        test("Apply Interest to All Accounts", () -> {
            BankService bank = new BankService("Test Bank");
            bank.createSavingsAccount("User 1", 1000.0);
            bank.createSavingsAccount("User 2", 2000.0);
            bank.createCheckingAccount("User 3", 3000.0);
            
            bank.applyInterestToAllAccounts();
            
            // Verify all accounts have more than initial deposit
            double total = bank.getTotalDeposits();
            assertTrue(total > 6000.0);
        });
    }

    // ==================== Overdraft Handling ====================
    private static void testOverdraftHandling() {
        printTestCategory("Overdraft Handling");

        // Test 1: Enter Overdraft
        test("Enter Overdraft on Withdrawal", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 100.0, 500.0);
            
            account.withdraw(300.0); // $200 overdraft used + $35 fee
            
            assertTrue(account.isInOverdraft());
            assertEqual(235.0, account.getCurrentOverdraft()); // 200 + 35 fee
        });

        // Test 2: Overdraft Fee Applied
        test("Overdraft Fee Applied", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 100.0, 500.0);
            
            account.withdraw(200.0); // Uses $100 overdraft, triggers $35 fee
            
            assertTrue(account.getTotalOverdraftFees() > 0);
        });

        // Test 3: Deposit Pays Off Overdraft First
        test("Deposit Pays Off Overdraft First", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 100.0, 500.0);
            
            account.withdraw(300.0); // Uses overdraft
            double overdraftBefore = account.getCurrentOverdraft();
            
            account.deposit(100.0); // Should reduce overdraft
            
            assertTrue(account.getCurrentOverdraft() < overdraftBefore);
        });

        // Test 4: Full Overdraft Repayment
        test("Full Overdraft Repayment", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 100.0, 500.0);
            
            account.withdraw(200.0); // Uses overdraft + fee
            double totalOwed = account.getCurrentOverdraft();
            
            account.deposit(totalOwed + 50.0); // Pay off plus extra
            
            assertFalse(account.isInOverdraft());
            assertEqual(50.0, account.getBalance()); // Balance should match the exact calculation
        });
    }

    // ==================== Exception Scenarios ====================
    private static void testExceptionScenarios() {
        printTestCategory("Exception Scenarios");

        // Test 1: Insufficient Funds in Savings
        test("Insufficient Funds - Savings (Below Minimum)", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 200.0);
            
            expectException(InsufficientFundsException.class, () -> {
                account.withdraw(150.0); // Would leave less than $100 minimum
            });
        });

        // Test 2: Insufficient Funds with Overdraft
        test("Insufficient Funds - Exceeds Overdraft Limit", () -> {
            BankService bank = new BankService("Test Bank");
            CheckingAccount account = bank.createCheckingAccount("Test User", 100.0, 200.0);
            
            expectException(InsufficientFundsException.class, () -> {
                account.withdraw(400.0); // Exceeds balance + overdraft
            });
        });

        // Test 3: Invalid Amount - Negative
        test("Invalid Amount - Negative Deposit", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            expectException(InvalidAmountException.class, () -> {
                account.deposit(-100.0);
            });
        });

        // Test 4: Account Not Found
        test("Account Not Found", () -> {
            BankService bank = new BankService("Test Bank");
            
            expectException(AccountNotFoundException.class, () -> {
                bank.getAccount("INVALID-9999");
            });
        });

        // Test 5: Transfer to Same Account
        test("Transfer to Same Account", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            expectException(TransferException.class, () -> {
                bank.transfer(account.getAccountNumber(), account.getAccountNumber(), 100.0);
            });
        });

        // Test 6: Withdrawal Limit Exceeded
        test("Savings Withdrawal Limit Exceeded", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 10000.0);
            
            // Make 6 withdrawals (the limit)
            for (int i = 0; i < 6; i++) {
                account.withdraw(100.0);
            }
            
            // 7th should fail
            expectException(WithdrawalLimitException.class, () -> {
                account.withdraw(100.0);
            });
        });
    }

    // ==================== Transaction History ====================
    private static void testTransactionHistory() {
        printTestCategory("Transaction History");

        // Test 1: Initial Deposit Recorded
        test("Initial Deposit in Transaction History", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            assertEqual(1, account.getTransactionHistory().size());
            assertEqual(Transaction.TransactionType.DEPOSIT, 
                    account.getTransactionHistory().get(0).getType());
        });

        // Test 2: Multiple Transactions Recorded
        test("Multiple Transactions Recorded", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            account.deposit(500.0);
            account.withdraw(200.0);
            account.deposit(100.0);
            
            assertEqual(4, account.getTransactionHistory().size());
        });

        // Test 3: Transaction Contains Correct Data
        test("Transaction Data Integrity", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            account.deposit(500.0);
            
            Transaction lastTxn = account.getTransactionHistory().get(1);
            
            assertEqual(500.0, lastTxn.getAmount());
            assertEqual(1500.0, lastTxn.getBalanceAfter());
            assertEqual(Transaction.TransactionType.DEPOSIT, lastTxn.getType());
        });

        // Test 4: Recent Transactions Filter
        test("Get Recent Transactions", () -> {
            BankService bank = new BankService("Test Bank");
            SavingsAccount account = bank.createSavingsAccount("Test User", 1000.0);
            
            for (int i = 0; i < 10; i++) {
                account.deposit(100.0);
            }
            
            assertEqual(5, account.getRecentTransactions(5).size());
        });
    }

    // ==================== Test Utilities ====================
    
    private static void test(String name, Runnable testCode) {
        testsRun++;
        try {
            testCode.run();
            testsPassed++;
            System.out.println("  ✓ " + name);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("  ✗ " + name);
            System.out.println("      Assertion failed: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("  ✗ " + name);
            System.out.println("      Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void assertEqual(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertEqual(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.01) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    private static <T extends Exception> void expectException(Class<T> exceptionClass, Runnable code) {
        try {
            code.run();
            throw new AssertionError("Expected " + exceptionClass.getSimpleName() + " but no exception was thrown");
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e)) {
                throw new AssertionError("Expected " + exceptionClass.getSimpleName() + 
                        " but got " + e.getClass().getSimpleName());
            }
        }
    }

    private static void printTestCategory(String category) {
        System.out.println("\n▶ " + category);
        System.out.println("─".repeat(50));
    }

    private static void printTestSummary() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      TEST SUMMARY                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests: %-47d║%n", testsRun);
        System.out.printf("║  Passed:      %-47d║%n", testsPassed);
        System.out.printf("║  Failed:      %-47d║%n", testsFailed);
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        
        if (testsFailed == 0) {
            System.out.println("║             ✓ ALL TESTS PASSED SUCCESSFULLY!                 ║");
        } else {
            System.out.printf("║             ✗ %d TEST(S) FAILED                               ║%n", testsFailed);
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
    }
}
