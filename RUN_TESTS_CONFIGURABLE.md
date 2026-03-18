# CepHire Configurable Test Runner

## 🎯 Overview
This test suite provides **complete control** over which tests to run using simple enable/disable flags in the TestNG XML file.

## 📋 Test Configuration

### Current Enabled Tests (enabled="true")
- ✅ **Valid Login Test** - Tests successful login flow
- ✅ **Invalid Login Test** - Tests login with wrong credentials  
- ✅ **Dashboard Navigation Test** - Tests dashboard access and elements
- ✅ **Logout Test** - Tests logout functionality

### Current Disabled Tests (enabled="false")
- ❌ **OAuth Buttons Test** - Tests Google/GitHub OAuth buttons
- ❌ **Candidates Tab Test** - Tests candidates tab functionality
- ❌ **Jobs Tab Test** - Tests jobs tab functionality
- ❌ **Search Functionality Test** - Tests search feature
- ❌ **Sidebar Navigation Test** - Tests sidebar navigation
- ❌ **Mobile Responsive Test** - Tests mobile view
- ❌ **Tablet Responsive Test** - Tests tablet view
- ❌ **Smoke Tests Suite** - All smoke tests together
- ❌ **All Login Tests** - All login tests together
- ❌ **All Dashboard Tests** - All dashboard tests together
- ❌ **All Navigation Tests** - All navigation tests together
- ❌ **All Responsive Tests** - All responsive tests together
- ❌ **Complete Test Suite** - All tests together

## 🚀 How to Run Tests

### Run Only Enabled Tests
```bash
mvn test -Dsurefire.suiteXmlFile=testng-configurable.xml
```

### Enable/Disable Tests
1. Open `testng-configurable.xml`
2. Find the test you want to enable/disable
3. Change `enabled="false"` to `enabled="true"` or vice versa
4. Save the file and run tests

### Example: Enable Mobile Responsive Tests
```xml
<test name="Mobile Responsive Test" enabled="true">
```

### Example: Disable Valid Login Test
```xml
<test name="Valid Login Test" enabled="false">
```

## 📊 Test Groups

### Available Groups
- **login** - All login-related tests
- **dashboard** - All dashboard tests
- **navigation** - All navigation tests
- **responsive** - All responsive design tests
- **smoke** - Critical smoke tests
- **regression** - Full regression tests
- **ui** - UI element tests
- **negative** - Negative test scenarios

### Run Specific Groups
```bash
# Run only login tests
mvn test -Dgroups=login -Dsurefire.suiteXmlFile=testng-configurable.xml

# Run only smoke tests
mvn test -Dgroups=smoke -Dsurefire.suiteXmlFile=testng-configurable.xml

# Run only dashboard tests
mvn test -Dgroups=dashboard -Dsurefire.suiteXmlFile=testng-configurable.xml
```

## 🖥️ Browser Configuration

### Change Browser
Edit the browser parameter in the XML file:
```xml
<parameter name="browser" value="chromium"/>
<!-- Options: chromium, firefox, webkit -->
```

### Headless Mode
Edit the headless parameter:
```xml
<parameter name="headless" value="false"/>
<!-- true = runs in background, false = shows browser -->
```

## 📁 Test Structure

### Single Test Class
All tests are in `CepHireCompleteTestSuite.java`:
- **Login Tests** - Authentication flows
- **Dashboard Tests** - Dashboard functionality
- **Navigation Tests** - Page navigation
- **Responsive Tests** - Mobile/tablet views

### One Browser at a Time
- `parallel="false"` - Only one browser instance
- `thread-count="1"` - Single thread execution
- Sequential test execution

## 🎛️ Quick Configuration Examples

### Quick Smoke Test
Enable only smoke tests:
```xml
<test name="Valid Login Test" enabled="true"/>
<test name="Dashboard Navigation Test" enabled="true"/>
<test name="Logout Test" enabled="true"/>
```

### Quick Regression Test
Enable all regression tests:
```xml
<test name="All Dashboard Tests" enabled="true"/>
<test name="All Navigation Tests" enabled="true"/>
```

### Quick Mobile Test
Enable only responsive tests:
```xml
<test name="Mobile Responsive Test" enabled="true"/>
<test name="Tablet Responsive Test" enabled="true"/>
```

## 📈 Reports

After running tests, check:
- **HTML Report**: `test-output/ExtentReport.html`
- **Screenshots**: `test-output/screenshots/`
- **Traces**: `test-output/traces/`

## 🔧 Troubleshooting

### Test Fails to Run
1. Check if test is enabled (`enabled="true"`)
2. Verify browser configuration
3. Check application URL (should be cepphire.com)

### Slow Test Execution
1. Set `headless="true"` for faster execution
2. Enable only necessary tests
3. Check network connectivity

### Browser Not Found
1. Install Playwright browsers:
   ```bash
   mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install chromium"
   ```

## 🎯 Best Practices

1. **Start Small**: Enable only 1-2 tests initially
2. **Test Groups**: Use groups to organize related tests
3. **Sequential**: Run tests one at a time for debugging
4. **Headless**: Use headless mode for CI/CD
5. **Reports**: Always check Extent Reports for results

## 📝 Quick Start

1. **Run current enabled tests**:
   ```bash
   mvn test -Dsurefire.suiteXmlFile=testng-configurable.xml
   ```

2. **Check results**:
   ```bash
   open test-output/ExtentReport.html
   ```

3. **Enable more tests** as needed in `testng-configurable.xml`

That's it! You now have complete control over which tests run, one browser at a time, on cepphire.com! 🎉
