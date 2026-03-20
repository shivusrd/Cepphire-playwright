# CepHire AI Web Automation Testing Framework - Project Structure

## 📋 Overview

A comprehensive web automation testing framework for CepHire AI using Java, Playwright, TestNG, and Extent Reports. This framework follows Page Object Model (POM) design patterns and provides clean, maintainable, and scalable test automation.

## 🚀 Technology Stack

- **Java 11+** - Programming language
- **Playwright** - Modern browser automation
- **TestNG** - Test framework with annotations and assertions
- **Maven** - Build and dependency management
- **Extent Reports** - Interactive HTML reporting
- **Jackson** - JSON parsing for test data
- **Page Object Model** - Design pattern for maintainable tests

## 📁 Complete Project Structure

```
CepHireAI-Playwright/
├── 📄 pom.xml                              # Maven configuration with dependencies
├── 📄 testng.xml                           # TestNG configuration (main test suite)
├── 📄 Jenkinsfile                          # CI/CD pipeline configuration
├── 📄 README.md                            # Project overview and quick start
├── 📄 PROJECT_STRUCTURE.md                 # This file - detailed structure documentation
├── 📄 HOW_TO_RUN_TESTS.md                  # Comprehensive testing guide
├── 📄 .gitignore                           # Git ignore rules
│
├── 📂 src/
│   └── 📂 test/
│       ├── 📂 java/
│       │   └── 📂 com/
│       │       └── 📂 cepphire/
│       │           ├── 📂 base/
│       │           │   └── 📄 BaseTest.java           # Base test class with Playwright setup
│       │           │
│       │           ├── 📂 listeners/
│       │           │   └── 📄 TestListener.java       # Extent Reports integration
│       │           │
│       │           ├── 📂 pages/                     # Page Object Model classes
│       │           │   ├── 📄 AuthPage.java           # Authentication page object
│       │           │   ├── 📄 DashboardPage.java      # Dashboard page object
│       │           │   ├── 📄 LoginFlowPage.java      # Complete login flow logic
│       │           │   └── 📄 ManageUsersPage.java     # User management page object
│       │           │
│       │           ├── 📂 tests/                     # Test classes
│       │           │   ├── 📄 CepHireLoginFlowTest.java     # Clean login flow test
│       │           │   └── 📄 UserCredentialLifecycleTest.java # User lifecycle test
│       │           │
│       │           └── 📂 utils/                     # Utility classes
│       │               ├── 📄 ConfigReader.java       # Configuration file reader
│       │               ├── 📄 JsonDataReader.java     # JSON test data parser
│       │               └── 📄 TestUtils.java           # Common test utilities
│       │
│       └── 📂 resources/
│           ├── 📄 config.properties                 # Test configuration (URLs, timeouts, etc.)
│           ├── 📂 testdata/
│           │   └── 📄 test-data.json              # Test data (credentials, users, etc.)
│           └── 📄 extent-config.xml             # Extent Reports configuration
│
└── 📂 test-output/                              # Generated test artifacts
    ├── 📄 ExtentReport.html                     # Interactive HTML test report
    ├── 📂 screenshots/                         # Failure screenshots
    ├── 📂 traces/                              # Playwright trace files for debugging
    └── 📂 surefire-reports/                    # Maven surefire XML reports
```

## 🏗️ Architecture Components

### 📂 Base Layer
- **BaseTest.java** - Core test infrastructure
  - Playwright browser initialization
  - Page and configuration management
  - Common setup/teardown methods
  - Browser configuration handling

### 📂 Page Object Model (POM)
- **AuthPage.java** - Authentication functionality
  - Login/logout operations
  - OAuth button interactions
  - Form validation and error handling
  
- **DashboardPage.java** - Dashboard functionality
  - Dashboard navigation and verification
  - User information display
  - Credits and admin access verification
  - Tab navigation (candidates, jobs)
  
- **LoginFlowPage.java** - Complete login flow orchestration
  - Three-phase login process (Authentication → Login → Dashboard)
  - Comprehensive Extent Reports integration
  - Error handling and debugging
  - Both IDE and TestNG execution support
  
- **ManageUsersPage.java** - User management functionality
  - Issue credentials to users
  - Revoke user access
  - Role assignment and user profile management
  - Dialog handling for confirmations

### 📂 Test Classes
- **CepHireLoginFlowTest.java** - Clean login validation
  - Single test method using LoginFlowPage
  - Simple assertions and verification
  - Minimal code, maximum readability
  
- **UserCredentialLifecycleTest.java** - Complete user lifecycle
  - Admin issues credentials → User initializes → Admin revokes access
  - Three-phase test scenario
  - Data-driven with externalized credentials

### 📂 Utilities
- **ConfigReader.java** - Configuration management
  - Properties file reading
  - Environment-specific configurations
  - URL and browser settings
  
- **JsonDataReader.java** - Test data management
  - JSON parsing for test data
  - Multiple user types and credentials
  - Organization and test scenario data
  
- **TestUtils.java** - Common test utilities
  - Wait conditions and timeouts
  - Element interaction helpers
  - Screenshot and debugging utilities

### 📂 Reporting & Listeners
- **TestListener.java** - Extent Reports integration
  - Automatic screenshot capture on failure
  - Test execution logging
  - HTML report generation
  - Playwright trace recording

## 📊 Test Data Management

### 📄 test-data.json Structure
```json
{
  "login": {
    "valid": {
      "admin": { "username": "admin@ukg.com", "password": "Shivu@srd1" },
      "admin01": { "username": "admin01@gmail.com", "password": "Shivu@srd1" },
      "superAdmin": { "username": "superadmin@cepphire.com", "password": "Admin@123" }
    },
    "invalid": { /* Wrong credentials scenarios */ }
  },
  "users": {
    "testRecruiter": {
      "email": "testrecruiter01@gmail.com",
      "password": "Shivu@srd1",
      "role": "recruiter",
      "displayName": "C CeppHire"
    }
  },
  "organization": { /* Organization test data */ },
  "urls": { /* Application URLs */ },
  "timeouts": { /* Test timeout configurations */ }
}
```

## ⚙️ Configuration System

### 📄 config.properties
```properties
# Application URLs
base.url=https://cepphire.com
auth.url=https://cepphire.com/auth
dashboard.url=https://cepphire.com/dashboard

# Browser Configuration
browser=chromium
headless=false
viewport.width=1280
viewport.height=720
timeout=30000

# Test Data
test.data.file.path=src/test/resources/testdata/test-data.json

# Reporting
extent.report.path=test-output/ExtentReport.html
capture.trace=true
capture.screenshot.on.failure=true
```

## 🎯 Test Categories

### 📋 Login Tests
- **Valid Login Flow** - Successful authentication and dashboard access
- **Invalid Login Scenarios** - Wrong credentials, error handling
- **OAuth Integration** - Google/GitHub authentication

### 📋 Dashboard Tests
- **Dashboard Access** - Post-login dashboard verification
- **User Information** - Email, credits, admin access
- **Navigation** - Sidebar, menu items, page transitions

### 📋 User Management Tests
- **Issue Credentials** - Admin creates user accounts
- **User Initialization** - First-time user setup
- **Revoke Access** - Admin removes user access
- **Role Management** - User role assignments

### 📋 Responsive Design Tests
- **Mobile View** - Mobile device compatibility
- **Tablet View** - Tablet device compatibility
- **Cross-browser** - Chrome, Firefox, Safari testing

## 🔄 Test Execution Flow

### 🚀 TestNG Configuration (testng.xml)
```xml
<suite name="CepHire Test Suite" parallel="tests" thread-count="1">
  <test name="Valid Login Flow Test">
    <classes>
      <class name="com.cepphire.tests.CepHireLoginFlowTest">
        <methods>
          <include name="testValidLoginFlow"/>
        </methods>
      </class>
    </classes>
  </test>
  
  <test name="User Credential Lifecycle Test">
    <classes>
      <class name="com.cepphire.tests.UserCredentialLifecycleTest">
        <methods>
          <include name="testUserCredentialLifecycle"/>
        </methods>
      </class>
    </classes>
  </test>
</suite>
```

## 📈 Reporting & Analytics

### 📊 Extent Reports Features
- **Interactive HTML Reports** - Clickable screenshots, expandable logs
- **Test Categorization** - Groups, tags, and priority levels
- **Execution Timeline** - Test duration and performance metrics
- **Error Analysis** - Stack traces, screenshots, and debugging info

### 🔍 Debugging Capabilities
- **Playwright Traces** - Step-by-step execution recording
- **Automatic Screenshots** - Captured on failures and key actions
- **Console Logs** - Browser console output integration
- **Network Monitoring** - Request/response tracking

## 🚀 CI/CD Integration

### 📄 Jenkinsfile Features
- **Multi-browser Support** - Parallel execution across browsers
- **Environment Selection** - Test, staging, production environments
- **Parameterized Builds** - Customizable test execution
- **Artifact Archiving** - Reports, screenshots, and traces
- **Failure Notifications** - Email and Slack integrations

## 🎛️ Customization & Extensibility

### ➕ Adding New Tests
1. Create test methods in existing test classes
2. Use existing page objects or create new ones
3. Add TestNG annotations (@Test, @BeforeMethod, etc.)
4. Update testng.xml if needed
5. Externalize test data in test-data.json

### ➕ Adding New Page Objects
1. Create new page class in `pages` package
2. Follow existing POM patterns
3. Include proper error handling and waits
4. Add Extent Reports logging
5. Use semantic locators and proper selectors

### ➕ Adding New Test Data
1. Update test-data.json with new scenarios
2. Enhance JsonDataReader.java if needed
3. Add configuration properties if required
4. Update ConfigReader.java for new properties

## 🔧 Development Best Practices

### ✅ Code Quality
- **Page Object Model** - Maintainable page abstraction
- **Externalized Data** - No hardcoded credentials or URLs
- **Semantic Locators** - Use Aria roles and meaningful selectors
- **Proper Waits** - Explicit waits for dynamic elements
- **Error Handling** - Comprehensive exception management

### ✅ Test Design
- **Single Responsibility** - Each test has one clear purpose
- **Independent Tests** - No dependencies between tests
- **Descriptive Names** - Clear, meaningful test method names
- **Test Groups** - Logical categorization for execution
- **Assertions** - Clear, specific validation messages

### ✅ Performance & Reliability
- **Parallel Execution** - Where appropriate
- **Retry Logic** - For flaky tests
- **Timeout Management** - Appropriate wait times
- **Resource Cleanup** - Proper teardown procedures
- **Memory Management** - Efficient object usage

## 🎯 Key Benefits

### 🏗️ Maintainability
- **Clean Architecture** - Well-organized, modular structure
- **POM Pattern** - Easy to maintain and extend
- **Externalized Configuration** - Environment-specific settings
- **Comprehensive Documentation** - Clear setup and usage guides

### 🚀 Scalability
- **Modular Design** - Easy to add new tests and pages
- **Data-Driven** - Support for multiple test scenarios
- **Cross-Browser** - Multi-browser compatibility
- **CI/CD Ready** - Automated pipeline integration

### 🔍 Debugging & Reporting
- **Rich Reports** - Detailed execution information
- **Visual Debugging** - Screenshots and traces
- **Error Analysis** - Comprehensive failure information
- **Performance Metrics** - Execution time and resource usage

---

## 📞 Support & Maintenance

This framework is actively maintained and designed for:
- **Easy Setup** - Quick installation and configuration
- **Clear Documentation** - Comprehensive guides and examples
- **Community Support** - Best practices and patterns
- **Continuous Improvement** - Regular updates and enhancements

**Ready for production testing on CepHire AI! 🚀**
