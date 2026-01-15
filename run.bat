@echo off
REM Bank Management System Run Script for Windows

echo.
echo Starting Bank Management System...
echo.

REM Check if compiled classes exist
if not exist "out\production\com\bank\Main.class" (
    echo Application not compiled. Running build first...
    call build.bat
    if %errorlevel% neq 0 exit /b 1
)

REM Run the application
java -cp out/production com.bank.Main
