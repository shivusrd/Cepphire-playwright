#!/bin/bash

echo "========================================"
echo "CepHire AI Sequential Test Runner"
echo "========================================"
echo ""
echo "This will run tests sequentially:"
echo "1. Test 1: Valid Login Flow (Chromium)"
echo "2. Test 2: User Credential Lifecycle (Firefox)"
echo ""
echo "Each test will run in its own browser session."
echo "========================================"
echo ""

echo "Running Test 1: Valid Login Flow in Chromium..."
echo "----------------------------------------"
mvn test -Dtest=CepHireLoginFlowTest#testValidLoginFlow -Dbrowser=chromium -Dheadless=false
echo ""

if [ $? -ne 0 ]; then
    echo "Test 1 failed! Stopping execution."
    exit 1
fi

echo "Test 1 completed successfully!"
echo ""
echo "Running Test 2: User Credential Lifecycle in Firefox..."
echo "-------------------------------------------------"
mvn test -Dtest=UserCredentialLifecycleTest#testUserCredentialLifecycle -Dbrowser=firefox -Dheadless=false
echo ""

if [ $? -ne 0 ]; then
    echo "Test 2 failed!"
    exit 1
fi

echo "========================================"
echo "All tests completed successfully!"
echo "========================================"
echo ""
echo "Check reports at: test-output/ExtentReport.html"
echo "Press Enter to continue..."
read -r
