@echo off
REM GitHub Actions Local Test Runner for Windows
REM This script simulates the GitHub Actions environment locally

echo 🚀 Starting GitHub Actions Local Test Runner
echo ==========================================

REM Default values (similar to GitHub Actions)
if "%BROWSER%"=="" set BROWSER=chromium
if "%HEADLESS%"=="" set HEADLESS=true
if "%BASE_URL%"=="" set BASE_URL=https://cepphire.com
if "%USER_EMAIL%"=="" set USER_EMAIL=admin@ukg.com
if "%USER_PASSWORD%"=="" set USER_PASSWORD=

REM Create test-output directory
echo 📁 Creating test-output directories...
if not exist "test-output\screenshots" mkdir "test-output\screenshots"
if not exist "test-output\traces" mkdir "test-output\traces"
if not exist "test-output\videos" mkdir "test-output\videos"
if not exist "test-output\reports" mkdir "test-output\reports"

REM Set environment variables
echo 🔧 Setting environment variables...
set BASE_URL=%BASE_URL%
set USER_EMAIL=%USER_EMAIL%
set USER_PASSWORD=%USER_PASSWORD%
set BROWSER=%BROWSER%
set HEADLESS=%HEADLESS%

REM Display configuration
echo 📋 Test Configuration:
echo   Browser: %BROWSER%
echo   Headless: %HEADLESS%
echo   Base URL: %BASE_URL%
echo   User Email: %USER_EMAIL%
echo   Output Directory: test-output\
echo.

REM Check if required tools are installed
echo 🔍 Checking dependencies...

REM Check Java
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Java not found. Please install Java 21 or later.
    exit /b 1
)

REM Check Maven
mvn -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Maven not found. Please install Maven.
    exit /b 1
)

echo ✅ Dependencies check passed
echo.

REM Install Playwright browsers (if needed)
echo 🌐 Installing Playwright browsers...
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"
if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to install Playwright browsers
    exit /b 1
)

echo ✅ Playwright browsers installed successfully
echo.

REM Clean previous results
echo 🧹 Cleaning previous test results...
mvn clean

REM Run tests
echo 🧪 Running Playwright tests...
echo ==========================================

mvn test ^
    -Dbrowser=%BROWSER% ^
    -Dheadless=%HEADLESS% ^
    -Dbase.url=%BASE_URL% ^
    -Ddefault.username=%USER_EMAIL% ^
    -Ddefault.password=%USER_PASSWORD% ^
    -Dcapture.screenshot.on.failure=true ^
    -Dcapture.trace=true ^
    -Dextent.reporter.html.start=true ^
    -Dci.environment=true

REM Check test results
echo.
echo 📊 Test Results Summary:
echo =======================

if exist "test-output\ExtentReport.html" (
    echo ✅ Extent Report generated: test-output\ExtentReport.html
) else (
    echo ❌ Extent Report not found
)

if exist "test-output\screenshots\*" (
    for /f %%i in ('dir /b "test-output\screenshots" 2^>nul ^| find /c /v ""') do set count=%%i
    echo 📸 Screenshots captured: %count% files
) else (
    echo 📸 No screenshots captured
)

if exist "test-output\traces\*" (
    for /f %%i in ('dir /b "test-output\traces" 2^>nul ^| find /c /v ""') do set count=%%i
    echo 🎥 Traces generated: %count% files
) else (
    echo 🎥 No traces generated
)

if exist "target\surefire-reports\TEST-TestSuite.xml" (
    echo 📄 Test results XML: target\surefire-reports\TEST-TestSuite.xml
) else (
    echo ❌ Test results XML not found
)

echo.
echo 🎯 Local CI test run completed!
echo ==============================
echo.
echo 📋 Next Steps:
echo 1. Review the Extent Report: test-output\ExtentReport.html
echo 2. Check screenshots in: test-output\screenshots\
echo 3. Analyze traces in: test-output\traces\
echo 4. Push to GitHub to trigger the actual CI/CD pipeline
echo.
echo 🔗 GitHub Actions will run with:
echo   - Multi-browser testing ^(Chromium, Firefox, WebKit^)
echo   - Security scanning
echo   - Artifact uploads
echo   - Comprehensive reporting

pause
