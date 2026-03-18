# CepHire AI Web Automation Testing Framework

A comprehensive web automation testing framework for CepHire AI using Java, Playwright, TestNG, and Extent Reports.

## 🚀 Tech Stack

- **Java 11** - Programming language
- **Playwright** - Browser automation
- **TestNG** - Test framework
- **Maven** - Build and dependency management
- **Extent Reports** - Test reporting
- **Jackson** - JSON parsing for test data

## 📁 Project Structure

```
automation-tests/
├── pom.xml                              # Maven configuration
├── testng.xml                           # TestNG configuration
├── Jenkinsfile                          # CI/CD pipeline
├── README.md                            # This file
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cepphire/
│   │   │           ├── base/
│   │   │           │   └── BaseTest.java      # Base test class
│   │   │           ├── pages/
│   │   │           │   ├── LoginPage.java     # Login page object
│   │   │           │   ├── SetupOrgPage.java  # Setup organization page object
│   │   │           │   └── DashboardPage.java # Dashboard page object
│   │   │           ├── tests/
│   │   │           │   └── CeppHireEndToEndTest.java # Main test class
│   │   │           ├── listeners/
│   │   │           │   └── TestListener.java  # Extent Reports listener
│   │   │           └── utils/
│   │   │               ├── ConfigReader.java   # Configuration reader
│   │   │               └── JsonDataReader.java # JSON test data reader
│   │   └── resources/
│   │       ├── config.properties              # Test configuration
│   │       ├── testdata/
│   │       │   └── test-data.json            # Test data
│   │       └── extent-config.xml             # Extent Reports config
└── test-output/                              # Generated reports and artifacts
```

## ⚙️ Configuration

### 1. Update `config.properties`

```properties
# Base URL for the application
base.url=http://localhost:3000

# Browser Configuration
browser=chromium
headless=false
viewport.width=1280
viewport.height=720
timeout=30000

# Test Data Configuration
test.data.file.path=src/test/resources/testdata/test-data.json

# Extent Reports Configuration
extent.report.path=test-output/ExtentReport.html

# Playwright Traces
trace.dir=test-output/traces
capture.trace=true

# Screenshots
screenshot.dir=test-output/screenshots
capture.screenshot.on.failure=true
```

### 2. Update Test Data

Edit `src/test/resources/testdata/test-data.json` with your test credentials:

```json
{
  "login": {
    "valid": {
      "admin": {
        "username": "admin@ukg.com",
        "password": "Shivu@srd1",
        "expectedUrl": "/dashboard"
      }
    }
  }
}
```

## 🏃‍♂️ Running Tests

### Prerequisites

1. **Java 11+** installed
2. **Maven 3.6+** installed
3. **Node.js** (for Playwright browser installation)

### Setup

1. Clone the repository
2. Navigate to the automation-tests directory
3. Install dependencies:
   ```bash
   mvn clean install
   ```

4. Install Playwright browsers:
   ```bash
   mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
   ```

### Running Tests

#### Run all tests:
```bash
mvn test
```

#### Run specific test groups:
```bash
# Smoke tests
mvn test -Dgroups=smoke

# Regression tests
mvn test -Dgroups=regression

# E2E tests
mvn test -Dgroups=e2e

# Multiple groups
mvn test -Dgroups="smoke,regression"
```

#### Run with specific browser:
```bash
# Firefox
mvn test -Dbrowser=firefox

# WebKit (Safari)
mvn test -Dbrowser=webkit

# Headless mode
mvn test -Dbrowser=chromium -Dheadless=true
```

#### Run specific test method:
```bash
mvn test -Dtest=CeppHireEndToEndTest#testSuccessfulLoginAndRedirectToSetupOrg
```

## 📊 Test Reports

After test execution, reports are generated in the `test-output/` directory:

- **ExtentReport.html** - Interactive HTML report with screenshots
- **surefire-reports/** - Maven surefire XML reports
- **screenshots/** - Failure screenshots
- **traces/** - Playwright trace files

## 🔧 TestNG Configuration

The `testng.xml` file provides flexible test configuration:

- **Parallel execution** across multiple browsers
- **Test groups** (smoke, regression, e2e, negative, security)
- **Parameterized tests** for different users and organization types
- **Cross-browser testing** support

### Test Groups

- **smoke** - Critical path tests
- **regression** - Full regression suite
- **e2e** - End-to-end scenarios
- **negative** - Invalid input scenarios
- **security** - Security-related tests
- **validation** - Form validation tests

## 🚀 CI/CD Integration

### Jenkins Pipeline

The included `Jenkinsfile` provides:

- **Multi-browser support** with parameter selection
- **Environment selection** (test, staging, production)
- **Headless/headed execution**
- **Test group selection**
- **Automatic artifact archiving**
- **HTML report publishing**
- **Failure analysis**

#### Jenkins Parameters

- **BROWSER** - chromium, firefox, webkit
- **ENVIRONMENT** - test, staging, production
- **HEADLESS** - true/false
- **TEST_GROUPS** - smoke, regression, e2e, negative, all

## 📝 Test Cases

### Main Test Scenarios

1. **testSuccessfulLoginAndRedirectToSetupOrg**
   - Login with valid credentials
   - Verify redirect to /setup-org
   - Verify setup-org page is displayed

2. **testOrganizationFormSubmissionAndPendingApproval**
   - Fill organization form
   - Submit form
   - Verify 'Pending Approval' status

3. **testDashboardAccessBlockedWhenNotApproved**
   - Try to access /dashboard
   - Verify access is blocked when not approved
   - Verify redirect to login/unauthorized

4. **testCompleteEndToEndFlow**
   - Complete flow from login to dashboard access check
   - Verify all steps work correctly

5. **testInvalidLoginCredentials**
   - Test wrong password
   - Test wrong username
   - Verify error messages

6. **testOrganizationFormValidation**
   - Test empty form submission
   - Test invalid website URL
   - Verify validation errors

## 🎯 Page Object Model

The framework uses the Page Object Model pattern:

- **LoginPage** - Login functionality
- **SetupOrgPage** - Organization setup functionality
- **DashboardPage** - Dashboard navigation and verification

## 🔍 Debugging

### Playwright Traces

Enable traces in `config.properties`:
```properties
capture.trace=true
```

Trace files are saved in `test-output/traces/` and can be opened in Playwright Inspector.

### Screenshots

Screenshots are automatically captured on failure and saved in `test-output/screenshots/`.

### Debug Mode

Run tests with debugging:
```bash
mvn test -Dbrowser=chromium -Dheadless=false
```

## 🛠️ Customization

### Adding New Tests

1. Create new test methods in `CeppHireEndToEndTest.java`
2. Use existing page objects or create new ones
3. Add appropriate TestNG annotations (@Test, @Parameters, etc.)
4. Update testng.xml if needed

### Adding New Page Objects

1. Create new page class in the `pages` package
2. Extend functionality from BaseTest
3. Follow the same pattern as existing page objects

### Updating Test Data

1. Modify `test-data.json` with new test data
2. Update `JsonDataReader.java` if adding new data types
3. Use the new data in your test methods

## 📈 Best Practices

1. **Use descriptive test method names**
2. **Add proper assertions with meaningful messages**
3. **Leverage TestNG groups for test categorization**
4. **Keep test data externalized**
5. **Use Page Object Model consistently**
6. **Add proper logging and error handling**
7. **Run tests in parallel for faster execution**
8. **Regularly update dependencies**

## 🐛 Troubleshooting

### Common Issues

1. **Browser not found**: Run `mvn playwright:install`
2. **Timeout errors**: Increase timeout in config.properties
3. **Element not found**: Check selectors in test-data.json
4. **Tests fail locally**: Verify application is running on correct URL

### Getting Help

1. Check test reports in `test-output/`
2. Review console logs for detailed error messages
3. Use Playwright traces for debugging
4. Check application logs for server-side issues

## 📄 License

This project is part of CepHire AI and follows the same licensing terms.

---

**Happy Testing! 🧪**
