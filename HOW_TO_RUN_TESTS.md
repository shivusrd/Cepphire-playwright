# How to Run Tests - CepHire AI Web Automation Testing Framework

## 📋 Overview

Comprehensive guide for setting up, configuring, and running automated tests for CepHire AI using Playwright, TestNG, and Maven.

## 🚀 Quick Start

### Prerequisites
- **Java 11+** installed and configured
- **Maven 3.6+** installed
- **Node.js** (for Playwright browser installation)
- **Git** (for version control)

### One-Command Setup
```bash
# Clone and setup
git clone <repository-url>
cd CepHireAI-Playwright
mvn clean install
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
mvn test
```

## 📦 Installation & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd CepHireAI-Playwright
```

### 2. Verify Java Version
```bash
java -version
# Should show Java 11 or higher
```

### 3. Install Maven Dependencies
```bash
mvn clean install
```

### 4. Install Playwright Browsers
```bash
# Install all browsers
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"

# Or install specific browser
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install chromium"
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install firefox"
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install webkit"
```

### 5. Verify Setup
```bash
# Run a simple test to verify everything works
mvn test -Dtest=CepHireLoginFlowTest#testValidLoginFlow
```

## ⚙️ Configuration

### 1. Update Application URLs
Edit `src/test/resources/config.properties`:

```properties
# Application URLs
base.url=https://cepphire.com
auth.url=https://cepphire.com/auth
dashboard.url=https://cepphire.com/dashboard
setup.org.url=https://cepphire.com/setup-org
candidate.dashboard.url=https://cepphire.com/candidate-dashboard
pending.approval.url=https://cepphire.com/pending-approval
```

### 2. Configure Browser Settings
```properties
# Browser Configuration
browser=chromium
headless=false
viewport.width=1280
viewport.height=720
timeout=30000
```

### 3. Update Test Credentials
Edit `src/test/resources/testdata/test-data.json`:

```json
{
  "login": {
    "valid": {
      "admin": {
        "username": "your-admin@cepphire.com",
        "password": "your-password",
        "expectedUrl": "/dashboard"
      },
      "admin01": {
        "username": "admin01@gmail.com",
        "password": "Shivu@srd1",
        "expectedUrl": "/dashboard"
      }
    }
  },
  "users": {
    "testRecruiter": {
      "email": "testrecruiter01@gmail.com",
      "password": "Shivu@srd1",
      "role": "recruiter",
      "displayName": "C CeppHire"
    }
  }
}
```

### 4. Configure Reporting
```properties
# Extent Reports Configuration
extent.report.path=test-output/ExtentReport.html
extent.report.config.path=src/test/resources/extent-config.xml

# Playwright Traces
trace.dir=test-output/traces
capture.trace=true

# Screenshots
screenshot.dir=test-output/screenshots
capture.screenshot.on.failure=true
```

## 🏃‍♂️ Running Tests

### Basic Test Execution

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=CepHireLoginFlowTest
mvn test -Dtest=UserCredentialLifecycleTest
```

#### Run Specific Test Method
```bash
mvn test -Dtest=CepHireLoginFlowTest#testValidLoginFlow
mvn test -Dtest=UserCredentialLifecycleTest#testUserCredentialLifecycle
```

### Test Groups

#### Available Test Groups
- **login** - Authentication and login tests
- **smoke** - Critical path tests
- **regression** - Full regression suite
- **users** - User management tests
- **lifecycle** - User lifecycle tests

#### Run by Groups
```bash
# Run only login tests
mvn test -Dgroups=login

# Run smoke tests
mvn test -Dgroups=smoke

# Run user lifecycle tests
mvn test -Dgroups=lifecycle

# Run multiple groups
mvn test -Dgroups="login,smoke"
```

### Browser Configuration

#### Run with Different Browsers
```bash
# Chrome (Chromium)
mvn test -Dbrowser=chromium

# Firefox
mvn test -Dbrowser=firefox

# Safari (WebKit)
mvn test -Dbrowser=webkit
```

#### Headless vs Headed Mode
```bash
# Headless mode (background execution)
mvn test -Dbrowser=chromium -Dheadless=true

# Headed mode (visible browser)
mvn test -Dbrowser=chromium -Dheadless=false
```

### TestNG Configuration

#### Using Custom TestNG XML
```bash
# Use default testng.xml
mvn test -Dsurefire.suiteXmlFile=testng.xml

# Use custom configuration
mvn test -Dsurefire.suiteXmlFile=path/to/custom-testng.xml
```

#### TestNG Parameters
```bash
# Set browser via TestNG
mvn test -Dbrowser=firefox -Dsurefire.suiteXmlFile=testng.xml

# Set headless mode
mvn test -Dheadless=true -Dsurefire.suiteXmlFile=testng.xml
```

## 📊 Test Reports

### Viewing Reports

#### Extent Reports (HTML)
```bash
# Open in default browser
open test-output/ExtentReport.html

# Or open manually
# Navigate to test-output/ExtentReport.html in your browser
```

#### Maven Surefire Reports
```bash
# XML reports location
ls test-output/surefire-reports/

# View in IDE or import to reporting tools
```

#### Screenshots
```bash
# View failure screenshots
ls test-output/screenshots/

# Open specific screenshot
open test-output/screenshots/test-failure.png
```

#### Playwright Traces
```bash
# View trace files
ls test-output/traces/

# Open trace in Playwright Inspector
playwright show-trace test-output/traces/trace.zip
```

### Report Configuration

#### Enable/Disable Features
```properties
# Enable traces
capture.trace=true

# Enable screenshots on failure
capture.screenshot.on.failure=true

# Disable screenshots on success
capture.screenshot.on.success=false
```

#### Custom Report Path
```properties
# Custom report location
extent.report.path=custom-reports/MyReport.html
screenshot.dir=custom-reports/screenshots
trace.dir=custom-reports/traces
```

## 🔧 Advanced Configuration

### Environment-Specific Configuration

#### Test Environment
```properties
# Environment setting
env=test

# Environment-specific URLs
base.url=https://test.cepphire.com
auth.url=https://test.cepphire.com/auth
```

#### Production Testing
```properties
# Production environment
env=production
base.url=https://cepphire.com
headless=true
```

### Parallel Execution

#### Configure Parallel Tests
```xml
<!-- In testng.xml -->
<suite name="CepHire Test Suite" parallel="tests" thread-count="2">
  <test name="Login Test">
    <parameter name="browser" value="chromium"/>
    <!-- test configuration -->
  </test>
  <test name="User Lifecycle Test">
    <parameter name="browser" value="firefox"/>
    <!-- test configuration -->
  </test>
</suite>
```

#### Run Parallel Tests
```bash
# Parallel execution with TestNG XML
mvn test -Dsurefire.suiteXmlFile=testng.xml

# Limit parallel threads
mvn test -Dsurefire.parallel.threads=2
```

### Performance Testing

#### Enable Performance Monitoring
```properties
# Performance settings
capture.performance.metrics=true
performance.threshold.slow=5000
performance.timeout.page=30000
```

#### Run Performance Tests
```bash
# Run with performance monitoring
mvn test -Dperformance.enabled=true -Dgroups=performance
```

## 🐛 Debugging

### Debug Mode

#### Run Tests in Debug Mode
```bash
# Headed mode for visual debugging
mvn test -Dbrowser=chromium -Dheadless=false

# With specific test
mvn test -Dtest=CepHireLoginFlowTest#testValidLoginFlow -Dbrowser=chromium -Dheadless=false
```

#### Enable Debug Logging
```properties
# Debug settings
debug.enabled=true
debug.level=verbose
log.browser.console=true
```

### Playwright Inspector

#### Generate Playwright Code
```bash
# Open Playwright Inspector
npx playwright codegen --target java https://cepphire.com

# Record specific scenario
npx playwright codegen --target java --device="Desktop Chrome" https://cepphire.com/auth
```

#### Debug with Traces
```bash
# Enable trace generation
mvn test -Dcapture.trace=true -Dtrace.level=all

# View trace after test
playwright show-trace test-output/traces/trace-1.zip
```

### Common Issues & Solutions

#### Browser Not Found
```bash
# Error: Browser not found
# Solution: Install Playwright browsers
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install chromium"
```

#### Timeout Issues
```bash
# Error: Timeout waiting for element
# Solution: Increase timeout in config.properties
timeout=60000
```

#### Element Not Found
```bash
# Error: Element not found
# Solutions:
# 1. Check application is running on correct URL
# 2. Verify selectors in page classes
# 3. Increase wait times
# 4. Run in headed mode to see actual page state
mvn test -Dbrowser=chromium -Dheadless=false
```

#### Authentication Failures
```bash
# Error: Login failed
# Solutions:
# 1. Verify credentials in test-data.json
# 2. Check application URL is correct
# 3. Verify user exists and has correct role
# 4. Check for CAPTCHA or 2FA requirements
```

## 🚀 CI/CD Integration

### Jenkins Pipeline

#### Basic Jenkinsfile
```groovy
pipeline {
    agent any
    
    parameters {
        choice(name: 'BROWSER', choices: ['chromium', 'firefox', 'webkit'], description: 'Browser to run tests')
        choice(name: 'ENVIRONMENT', choices: ['test', 'staging', 'production'], description: 'Target environment')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run in headless mode')
    }
    
    stages {
        stage('Setup') {
            steps {
                sh 'mvn clean install'
                sh 'mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install ${params.BROWSER}"'
            }
        }
        
        stage('Test') {
            steps {
                sh """
                    mvn test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Denvironment=${params.ENVIRONMENT}
                """
            }
        }
        
        stage('Reports') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Report'
                ])
                
                archiveArtifacts artifacts: 'test-output/**/*', fingerprint: true
            }
        }
    }
}
```

### GitHub Actions

#### GitHub Actions Workflow
```yaml
name: Playwright Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        browser: [chromium, firefox, webkit]
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Setup Java
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
    
    - name: Install dependencies
      run: mvn clean install
    
    - name: Install Playwright browsers
      run: mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install ${{ matrix.browser }}"
    
    - name: Run tests
      run: mvn test -Dbrowser=${{ matrix.browser }} -Dheadless=true
    
    - name: Upload reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: playwright-reports-${{ matrix.browser }}
        path: test-output/
```

## 📈 Best Practices

### Test Organization

#### Use Descriptive Test Names
```java
@Test(description = "Complete user credential lifecycle - Issue, Initialize, and Revoke")
public void testUserCredentialLifecycle() {
    // Test implementation
}
```

#### Group Tests Logically
```java
@Test(groups = {"login", "smoke", "regression"})
public void testValidLoginFlow() {
    // Critical login test
}

@Test(groups = {"users", "lifecycle", "regression"})
public void testUserCredentialLifecycle() {
    // User management test
}
```

#### Externalize Test Data
```java
// Good: Use test data reader
String username = testData.getUsername("admin");
String password = testData.getPassword("admin");

// Bad: Hardcoded values
String username = "admin@ukg.com";
String password = "hardcoded-password";
```

### Performance Optimization

#### Run Tests in Parallel
```xml
<!-- Configure parallel execution -->
<suite name="CepHire Test Suite" parallel="tests" thread-count="4">
```

#### Use Headless Mode for CI/CD
```bash
# Fast execution in CI/CD
mvn test -Dbrowser=chromium -Dheadless=true
```

#### Optimize Wait Times
```java
// Use specific waits instead of fixed timeouts
page.waitForSelector("#dashboard-content");
page.waitForLoadState();

// Avoid excessive fixed waits
page.waitForTimeout(10000); // Avoid this
```

### Maintenance

#### Regular Updates
```bash
# Update dependencies regularly
mvn versions:display-dependency-updates
mvn versions:use-latest-releases

# Update Playwright
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"
```

#### Clean Test Output
```bash
# Clean old reports
mvn clean

# Remove old artifacts
rm -rf test-output/
```

#### Version Control
```gitignore
# Ignore test outputs
test-output/
*.log
reports/
screenshots/
traces/
```

## 📞 Troubleshooting Guide

### Environment Issues

#### Java Version Problems
```bash
# Check Java version
java -version

# Set JAVA_HOME (if needed)
export JAVA_HOME=/path/to/java11
```

#### Maven Issues
```bash
# Clean Maven cache
mvn dependency:purge-local-repository

# Force update dependencies
mvn clean install -U
```

#### Playwright Issues
```bash
# Reinstall Playwright
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --force"

# Check Playwright version
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="--version"
```

### Test Failures

#### Intermittent Failures
```bash
# Add retry logic in testng.xml
<listeners>
    <listener class-name="com.cepphire.listeners.RetryAnalyzer"/>
</listeners>

# Or run with retry
mvn test -Dretry.count=3
```

#### Network Issues
```bash
# Increase timeouts
timeout=60000

# Use local testing
base.url=http://localhost:3000
```

#### Browser Issues
```bash
# Clear browser cache
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --force"

# Use different browser
mvn test -Dbrowser=firefox
```

## 🎉 Summary

You now have a complete understanding of how to:
- ✅ **Set up** the testing environment
- ✅ **Configure** tests for different scenarios
- ✅ **Run tests** with various options and parameters
- ✅ **Debug issues** using built-in tools and techniques
- ✅ **Integrate** with CI/CD pipelines
- ✅ **Maintain** the framework for long-term use

**Happy Testing! 🚀**

For additional support, check the PROJECT_STRUCTURE.md file or refer to the test reports generated after execution.
