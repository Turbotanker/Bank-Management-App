@echo off
REM Bank Management System Build Script for Windows

echo.
echo ══════════════════════════════════════════════════════════════
echo        BANK MANAGEMENT SYSTEM - BUILD SCRIPT
echo ══════════════════════════════════════════════════════════════
echo.

REM Create output directories
if not exist "out\production" mkdir out\production
if not exist "out\test" mkdir out\test

echo [1/3] Cleaning previous build...
del /Q out\production\*.class 2>nul
del /Q out\test\*.class 2>nul

echo [2/3] Compiling main source files...
javac -d out/production -sourcepath src/main/java src/main/java/com/bank/Main.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    exit /b 1
)

echo [3/3] Compiling test files...
javac -d out/test -cp out/production -sourcepath src/test/java src/test/java/com/bank/BankManagementTest.java
if %errorlevel% neq 0 (
    echo ERROR: Test compilation failed!
    exit /b 1
)

echo.
echo ══════════════════════════════════════════════════════════════
echo BUILD SUCCESSFUL!
echo.
echo To run the application:
echo   java -cp out/production com.bank.Main
echo.
echo To run the tests:
echo   java -cp "out/production;out/test" com.bank.BankManagementTest
echo ══════════════════════════════════════════════════════════════
echo.
