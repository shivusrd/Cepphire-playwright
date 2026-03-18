# GitHub Actions CI/CD Setup for Playwright Java Tests

This document explains how to set up and use the GitHub Actions workflow for running Playwright Java tests in the cloud.

## 🚀 Features

- **Multi-Browser Testing**: Tests run on Chromium, Firefox, and WebKit
- **Parallel Execution**: Tests run in parallel across different browsers
- **Artifact Management**: Automatic upload of Extent Reports, screenshots, traces, and videos
- **Security Scanning**: Integrated vulnerability scanning with Trivy
- **Manual Triggers**: Support for manual workflow runs with configurable options
- **Secrets Management**: Secure credential handling using GitHub Secrets

## 📋 Prerequisites

1. Fork or create a repository on GitHub
2. Ensure your Playwright Java tests are properly structured
3. Have a `pom.xml` file with Playwright dependencies

## 🔧 Setup Instructions

### 1. Repository Structure

Make sure your repository has this structure:
```
your-repo/
├── .github/
│   └── workflows/
│       └── playwright.yml
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── yourpackage/
│                   ├── tests/
│                   ├── pages/
│                   └── base/
├── pom.xml
└── README.md
```

### 2. Configure GitHub Secrets

Navigate to your GitHub repository → Settings → Secrets and variables → Actions → New repository secret

Add the following secrets:

#### Required Secrets:

| Secret Name | Description | Example Value |
|--------------|-------------|---------------|
| `BASE_URL` | Base URL for your application | `https://cepphire.com` |
| `USER_EMAIL` | Test user email | `admin@ukg.com` |
| `USER_PASSWORD` | Test user password | `your-secure-password` |

#### Optional Secrets:

| Secret Name | Description | Default Value |
|--------------|-------------|---------------|
| `EXTENT_REPORTER_HTML_START` | Enable Extent Reports | `true` |
| `CAPTURE_SCREENSHOT_ON_FAILURE` | Capture screenshots on failure | `true` |
| `CAPTURE_TRACE` | Enable Playwright traces | `true` |

### 3. Workflow Configuration

The workflow is configured to:

#### **Triggers:**
- **Push to main/develop**: Automatic execution
- **Pull requests to main**: Automatic execution  
- **Manual dispatch**: Manual execution with browser selection

#### **Environment Setup:**
- **OS**: Ubuntu Latest
- **Java**: JDK 21 (Temurin)
- **Caching**: Maven dependencies and Playwright browsers
- **System Dependencies**: All required Playwright system libraries

#### **Test Execution:**
- **Browsers**: Chromium, Firefox, WebKit (parallel)
- **Mode**: Headless execution
- **Timeout**: 60 minutes
- **Artifacts**: Reports, screenshots, traces, videos

## 🎯 Usage

### Automatic Runs

The workflow automatically runs when:
- Code is pushed to `main` or `develop` branches
- Pull requests are created targeting `main`

### Manual Runs

1. Go to your repository → Actions tab
2. Select "Playwright Java Tests" workflow
3. Click "Run workflow"
4. Configure options:
   - **Browser**: Choose which browser to test (chromium/firefox/webkit)
   - **Headless**: Enable/disable headless mode

### Viewing Results

#### **Artifacts Download:**
After each run, download artifacts from the Actions tab:

1. **Extent Reports**: `extent-reports-{browser}` - HTML test reports
2. **Screenshots**: `extent-reports-{browser}` - Failure screenshots
3. **Traces**: `playwright-traces-{browser}` - Playwright execution traces
4. **Videos**: `playwright-videos-{browser}` - Test execution videos
5. **Test Results**: `test-results-{browser}` - XML test results

#### **Report Analysis:**
- Open `ExtentReport.html` in your browser for detailed test reports
- Use Playwright traces to debug test failures
- Review screenshots for visual verification

## 🔍 Workflow Details

### Jobs Overview

#### **1. Test Job**
- **Strategy Matrix**: Runs tests across all browsers in parallel
- **Steps**:
  1. Checkout code
  2. Setup JDK 21
  3. Cache dependencies
  4. Install Playwright browsers
  5. Run tests with Maven
  6. Upload artifacts

#### **2. Security Scan Job**
- **Trigger**: Only on pushes to main branch
- **Tool**: Trivy vulnerability scanner
- **Output**: SARIF report uploaded to GitHub Security tab

#### **3. Notify Job**
- **Trigger**: Always runs after test completion
- **Purpose**: Status notification and summary

### Environment Variables

The workflow uses these environment variables:

```yaml
env:
  BASE_URL: ${{ secrets.BASE_URL || 'https://cepphire.com' }}
  USER_EMAIL: ${{ secrets.USER_EMAIL }}
  USER_PASSWORD: ${{ secrets.USER_PASSWORD }}
```

### Maven Configuration

Tests run with these Maven properties:

```bash
mvn test \
  -Dbrowser=${{ matrix.browser }} \
  -Dheadless=true \
  -Dbase.url=${{ env.BASE_URL }} \
  -Ddefault.username=${{ env.USER_EMAIL }} \
  -Ddefault.password=${{ env.USER_PASSWORD }} \
  -Dcapture.screenshot.on.failure=true \
  -Dcapture.trace=true \
  -Dextent.reporter.html.start=true
```

## 🛠️ Customization

### Adding New Browsers

To add a new browser, update the matrix strategy:

```yaml
strategy:
  matrix:
    browser: [chromium, firefox, webkit, edge]  # Add edge
    include:
      - browser: chromium
        browser-name: "Chromium"
      - browser: firefox
        browser-name: "Firefox"
      - browser: webkit
        browser-name: "WebKit"
      - browser: edge  # Add this
        browser-name: "Edge"
```

### Modifying Test Parameters

Update the test execution step to pass different Maven properties:

```yaml
- name: Run Playwright Tests
  run: |
    mvn test \
      -Dbrowser=${{ matrix.browser }} \
      -Dheadless=${{ github.event.inputs.headless || 'true' }} \
      -Dbase.url=${{ env.BASE_URL }} \
      -Ddefault.username=${{ env.USER_EMAIL }} \
      -Ddefault.password=${{ env.USER_PASSWORD }} \
      -Dyour.custom.property=value
```

### Adding Custom Artifacts

To upload additional files:

```yaml
- name: Upload Custom Artifacts
  uses: actions/upload-artifact@v4
  if: always()
  with:
    name: custom-artifacts-${{ matrix.browser }}
    path: path/to/your/files/
    retention-days: 30
```

## 🐛 Troubleshooting

### Common Issues

#### **1. Playwright Browser Installation Failed**
- **Solution**: Ensure system dependencies are installed
- **Check**: The "Install system dependencies" step

#### **2. Tests Fail Due to Missing Secrets**
- **Solution**: Configure all required secrets in repository settings
- **Check**: Settings → Secrets and variables → Actions

#### **3. Artifact Upload Fails**
- **Solution**: Ensure test-output directory exists and has proper permissions
- **Check**: The "Create test-output directory" step

#### **4. Tests Timeout**
- **Solution**: Increase timeout-minutes in workflow
- **Check**: The `timeout-minutes: 60` setting

### Debugging Tips

1. **Check Workflow Logs**: Review each step's output in Actions tab
2. **Download Test Results**: Analyze XML reports and Extent Reports
3. **Review Playwright Traces**: Use traces to understand test failures
4. **Check Artifacts**: Ensure all expected files are uploaded

## 📊 Monitoring

### Workflow Performance

- **Caching**: Maven dependencies and Playwright browsers are cached
- **Parallel Execution**: Tests run across multiple browsers simultaneously
- **Optimized Steps**: Only necessary steps run based on trigger conditions

### Cost Optimization

- **Artifact Retention**: Limited to 7-30 days to reduce storage costs
- **Security Scan**: Only runs on main branch pushes
- **Conditional Execution**: Steps skip when not needed

## 🔗 Integration

### With CI/CD Pipeline

This workflow can be integrated with:
- **Deployment pipelines**: Run tests before deployment
- **Release processes**: Ensure quality before releases
- **Monitoring systems**: Send test results to monitoring tools

### With External Services

- **Slack Notifications**: Add Slack integration for status updates
- **Email Notifications**: Configure email alerts for test failures
- **Dashboard Integration**: Send results to test management tools

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Extent Reports Documentation](https://www.extentreports.com/)

## 🤝 Contributing

When contributing to this workflow:

1. Test changes locally first
2. Use feature branches for major changes
3. Update documentation for any configuration changes
4. Test the workflow in a fork before merging

## 📞 Support

For issues with this workflow:

1. Check the troubleshooting section
2. Review GitHub Actions logs
3. Create an issue with detailed error information
4. Include workflow run ID and relevant logs
