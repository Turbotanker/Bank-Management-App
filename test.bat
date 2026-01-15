@echo off
REM Bank Management System Test Script for Windows

echo.
echo Running Bank Management System Tests...
echo.

REM Check if compiled classes exist
if not exist "out\test\com\bank\BankManagementTest.class" (
    echo Tests not compiled. Running build first...
    call build.bat
    if %errorlevel% neq 0 exit /b 1
)

REM Run the tests
java -cp "out/production;out/test" com.bank.BankManagementTest
