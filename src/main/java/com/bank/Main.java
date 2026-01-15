package com.bank;

import com.bank.service.BankService;
import com.bank.ui.BankCLI;

/**
 * Main entry point for the Bank Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the bank service
        BankService bankService = new BankService("First National Bank");
        
        // Create some demo accounts for testing
        createDemoAccounts(bankService);
        
        // Start the CLI interface
        BankCLI cli = new BankCLI(bankService);
        cli.start();
    }

    private static void createDemoAccounts(BankService bankService) {
        // Create demo accounts for easy testing
        System.out.println("\n[Creating demo accounts for testing...]");
        
        var savings1 = bankService.createSavingsAccount("John Smith", 5000.00);
        System.out.println("  Created: " + savings1);
        
        var savings2 = bankService.createSavingsAccount("Jane Doe", 10000.00, 0.035);
        System.out.println("  Created: " + savings2);
        
        var checking1 = bankService.createCheckingAccount("John Smith", 2500.00);
        System.out.println("  Created: " + checking1);
        
        var checking2 = bankService.createCheckingAccount("Bob Wilson", 1000.00, 1000.00);
        System.out.println("  Created: " + checking2);
        
        System.out.println("\n[Demo accounts created successfully!]");
    }
}
