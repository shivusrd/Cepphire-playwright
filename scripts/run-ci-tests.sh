#!/bin/bash

# GitHub Actions Local Test Runner
# This script simulates the GitHub Actions environment locally

set -e

echo "🚀 Starting GitHub Actions Local Test Runner"
echo "=========================================="

# Default values (similar to GitHub Actions)
BROWSER=${BROWSER:-"chromium"}
HEADLESS=${HEADLESS:-"true"}
BASE_URL=${BASE_URL:-"https://cepphire.com"}
USER_EMAIL=${USER_EMAIL:-"admin@ukg.com"}
USER_PASSWORD=${USER_PASSWORD:-""}

# Create test-output directory
echo "📁 Creating test-output directories..."
mkdir -p test-output/screenshots test-output/traces test-output/videos test-output/reports

# Set environment variables
echo "🔧 Setting environment variables..."
export BASE_URL="$BASE_URL"
export USER_EMAIL="$USER_EMAIL"
export USER_PASSWORD="$USER_PASSWORD"
export BROWSER="$BROWSER"
export HEADLESS="$HEADLESS"

# Display configuration
echo "📋 Test Configuration:"
echo "  Browser: $BROWSER"
echo "  Headless: $HEADLESS"
echo "  Base URL: $BASE_URL"
echo "  User Email: $USER_EMAIL"
echo "  Output Directory: test-output/"
echo ""

# Check if required tools are installed
echo "🔍 Checking dependencies..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 21 or later."
    exit 1
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven."
    exit 1
fi

echo "✅ Dependencies check passed"
echo ""

# Install Playwright browsers (if needed)
echo "🌐 Installing Playwright browsers..."
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps" || {
    echo "❌ Failed to install Playwright browsers"
    exit 1
}

echo "✅ Playwright browsers installed successfully"
echo ""

# Clean previous results
echo "🧹 Cleaning previous test results..."
mvn clean

# Run tests
echo "🧪 Running Playwright tests..."
echo "=========================================="

mvn test \
    -Dbrowser="$BROWSER" \
    -Dheadless="$HEADLESS" \
    -Dbase.url="$BASE_URL" \
    -Ddefault.username="$USER_EMAIL" \
    -Ddefault.password="$USER_PASSWORD" \
    -Dcapture.screenshot.on.failure=true \
    -Dcapture.trace=true \
    -Dextent.reporter.html.start=true \
    -Dci.environment=true

# Check test results
echo ""
echo "📊 Test Results Summary:"
echo "======================="

if [ -f "test-output/ExtentReport.html" ]; then
    echo "✅ Extent Report generated: test-output/ExtentReport.html"
else
    echo "❌ Extent Report not found"
fi

if [ -d "test-output/screenshots" ] && [ "$(ls -A test-output/screenshots)" ]; then
    echo "📸 Screenshots captured: $(ls test-output/screenshots | wc -l) files"
else
    echo "📸 No screenshots captured"
fi

if [ -d "test-output/traces" ] && [ "$(ls -A test-output/traces)" ]; then
    echo "🎥 Traces generated: $(ls test-output/traces | wc -l) files"
else
    echo "🎥 No traces generated"
fi

if [ -f "target/surefire-reports/TEST-TestSuite.xml" ]; then
    echo "📄 Test results XML: target/surefire-reports/TEST-TestSuite.xml"
    
    # Extract test statistics
    if command -v xmllint &> /dev/null; then
        echo ""
        echo "📈 Test Statistics:"
        xmllint --xpath "//testsuite/@tests | //testsuite/@failures | //testsuite/@errors | //testsuite/@skipped" target/surefire-reports/TEST-TestSuite.xml 2>/dev/null || echo "Could not parse test statistics"
    fi
else
    echo "❌ Test results XML not found"
fi

echo ""
echo "🎯 Local CI test run completed!"
echo "=============================="
echo ""
echo "📋 Next Steps:"
echo "1. Review the Extent Report: test-output/ExtentReport.html"
echo "2. Check screenshots in: test-output/screenshots/"
echo "3. Analyze traces in: test-output/traces/"
echo "4. Push to GitHub to trigger the actual CI/CD pipeline"
echo ""
echo "🔗 GitHub Actions will run with:"
echo "  - Multi-browser testing (Chromium, Firefox, WebKit)"
echo "  - Security scanning"
echo "  - Artifact uploads"
echo "  - Comprehensive reporting"
