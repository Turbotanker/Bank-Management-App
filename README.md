# Bank Management System

A comprehensive Java OOP-based bank management application demonstrating core object-oriented programming principles including **Inheritance**, **Polymorphism**, **Abstraction**, and **Encapsulation**.

## Architecture

```
src/
    main/java/com/bank/
        model/
            Account.java          # Abstract base class
            SavingsAccount.java   # Savings with interest & withdrawal limits
            CheckingAccount.java  # Checking with overdraft protection
            Transaction.java      # Transaction record with timestamp
        service/
            BankService.java      # Core banking operations & transfers
        exception/
            BankingException.java
            InsufficientFundsException.java
            InvalidAmountException.java
            AccountNotFoundException.java
            WithdrawalLimitException.java
            TransferException.java
        ui/
            BankCLI.java          # Interactive command-line interface
        Main.java                 # Application entry point
    test/java/com/bank/
        BankManagementTest.java   # Comprehensive mock test suite
```

## Features

### Account Types
- **Savings Account**
  - 2.5% default annual interest rate (configurable)
  - $100 minimum balance requirement
  - 6 withdrawals per month limit
  - Interest compounds monthly

- **Checking Account**
  - Overdraft protection (default $500 limit, configurable)
  - $35 overdraft fee when entering overdraft
  - Deposits automatically pay off overdraft first
  - Minimal interest (0.1% annual)

### Banking Operations
- Create accounts (Savings/Checking)
- Deposit and withdraw funds
- Transfer between accounts
- View account details
- Transaction history with timestamps
- Monthly interest application
- Bank-wide summary reports

### OOP Concepts Demonstrated
- **Abstraction**: Abstract `Account` class with template methods
- **Inheritance**: `SavingsAccount` and `CheckingAccount` extend `Account`
- **Polymorphism**: `canWithdraw()`, `applyInterest()` behave differently per account type
- **Encapsulation**: Private fields with controlled access via methods

## Quick Start

### Prerequisites
- Java JDK 17 or higher
- Windows PowerShell or Command Prompt

### Build & Run

**Option 1: Using batch scripts (recommended)**

```powershell
# Build the project
.\build.bat

# Run the application
.\run.bat

# Run the tests
.\test.bat
```

**Option 2: Manual commands**

```powershell
# Create output directories
mkdir -p out/production
mkdir -p out/test

# Compile main source
javac -d out/production -sourcepath src/main/java src/main/java/com/bank/Main.java

# Run the application
java -cp out/production com.bank.Main

# Compile and run tests
javac -d out/test -cp out/production -sourcepath src/test/java src/test/java/com/bank/BankManagementTest.java
java -cp "out/production;out/test" com.bank.BankManagementTest
```

## CLI Menu

```
═══════════════════ MAIN MENU ══════════════════
  1. Create Account
  2. View Account Details
  3. Deposit Funds
  4. Withdraw Funds
  5. Transfer Funds
  6. View Transaction History
  7. Apply Monthly Interest
  8. List All Accounts
  9. Bank Summary Report
  0. Exit
════════════════════════════════════════════════
```

## Test Coverage

The test suite (`BankManagementTest.java`) covers:
- Account creation (Savings & Checking)
- Deposit and withdrawal operations
- Transfer between accounts
- Interest calculations
- Overdraft handling and fees
- Exception scenarios (insufficient funds, invalid amounts, etc.)
- Transaction history integrity

## Sample Output

```
╔══════════════════════════════════════════════════════════════╗
║  First National Bank - Summary Report                        ║
╠══════════════════════════════════════════════════════════════╣
║  Total Accounts: 4                                           ║
║  Savings Accounts: 2                                         ║
║  Checking Accounts: 2                                        ║
╠══════════════════════════════════════════════════════════════╣
║  Total Savings:              $15,000.00                      ║
║  Total Checking:              $3,500.00                      ║
║  Total Overdraft Used:            $0.00                      ║
║  Grand Total:                $18,500.00                      ║
╚══════════════════════════════════════════════════════════════╝
```

## Tech Stack

- **Language**: Java 17+
- **Paradigm**: Object-Oriented Programming
- **Design Patterns**: Factory (account creation), Template Method (withdrawal flow)
- **Version Control**: Git & GitHub ready

## License

MIT License - Feel free to use and modify for learning purposes.
