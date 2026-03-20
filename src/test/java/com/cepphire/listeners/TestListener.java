package com.cepphire.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.Page;
import com.cepphire.base.BaseTest;
import com.cepphire.utils.ConfigReader;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class TestListener implements ITestListener {
    
    private static final String REPORT_PATH = "test-output/ExtentReport.html";
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    static {
        // Create directories if they don't exist
        new File(SCREENSHOT_DIR).mkdirs();
        
        // Initialize Extent Reports with enhanced configuration
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
        sparkReporter.config().setDocumentTitle("CepHire AI Automation Test Report");
        sparkReporter.config().setReportName("Web Automation Test Results");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("UTF-8");
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
        
        // Add custom CSS for better screenshot viewing
        sparkReporter.config().setCss(".screenshot-modal { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.9); z-index: 9999; display: none; cursor: pointer; } " +
                                       ".screenshot-modal img { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); max-width: 95%; max-height: 95%; border: 3px solid #fff; border-radius: 8px; } " +
                                       ".screenshot-modal .close-btn { position: absolute; top: 20px; right: 40px; color: #fff; font-size: 40px; font-weight: bold; cursor: pointer; z-index: 10000; } " +
                                       ".screenshot-thumb { cursor: pointer; transition: transform 0.2s; } " +
                                       ".screenshot-thumb:hover { transform: scale(1.05); } ");
        
        // Add custom JavaScript for modal functionality
        sparkReporter.config().setJs("function createScreenshotModal() { " +
                                   "  const modal = document.createElement('div'); " +
                                   "  modal.className = 'screenshot-modal'; " +
                                   "  modal.innerHTML = '<span class=\"close-btn\">&times;</span><img src=\"\" alt=\"Screenshot\">'; " +
                                   "  document.body.appendChild(modal); " +
                                   "  " +
                                   "  modal.addEventListener('click', function() { " +
                                   "    modal.style.display = 'none'; " +
                                   "  }); " +
                                   "  " +
                                   "  modal.querySelector('.close-btn').addEventListener('click', function(e) { " +
                                   "    e.stopPropagation(); " +
                                   "    modal.style.display = 'none'; " +
                                   "  }); " +
                                   "  " +
                                   "  return modal; " +
                                   "} " +
                                   "" +
                                   "function showScreenshotInModal(imageSrc) { " +
                                   "  let modal = document.querySelector('.screenshot-modal'); " +
                                   "  if (!modal) { " +
                                   "    modal = createScreenshotModal(); " +
                                   "  } " +
                                   "  modal.querySelector('img').src = imageSrc; " +
                                   "  modal.style.display = 'block'; " +
                                   "} " +
                                   "" +
                                   "// Initialize modal when page loads " +
                                   "document.addEventListener('DOMContentLoaded', function() { " +
                                   "  createScreenshotModal(); " +
                                   "});");
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // Load configuration for environment variables
        Properties config = null;
        try {
            config = ConfigReader.getProperties();
        } catch (Exception e) {
            System.err.println("Warning: Could not load config properties for environment info");
        }
        
        // Add comprehensive system information
        extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        extent.setSystemInfo("OS Architecture", System.getProperty("os.arch"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Java Vendor", System.getProperty("java.vendor"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("User Home", System.getProperty("user.home"));
        extent.setSystemInfo("Environment", System.getProperty("env", "test"));
        extent.setSystemInfo("Browser", System.getProperty("browser", "chromium"));
        extent.setSystemInfo("Headless Mode", System.getProperty("headless", "false"));
        extent.setSystemInfo("Test Execution Time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        // Add application-specific environment variables
        if (config != null) {
            extent.setSystemInfo("Application Base URL", config.getProperty("base.url", "https://cepphire.com"));
            extent.setSystemInfo("Auth URL", config.getProperty("auth.url", "https://cepphire.com/auth"));
            extent.setSystemInfo("Dashboard URL", config.getProperty("dashboard.url", "https://cepphire.com/dashboard"));
            extent.setSystemInfo("Default Username", config.getProperty("default.username", "admin@ukg.com"));
            extent.setSystemInfo("Default Credits", config.getProperty("default.credits", "22 Credits"));
            extent.setSystemInfo("Default Candidates", config.getProperty("default.candidates", "5 candidates"));
            extent.setSystemInfo("Test Timeout", config.getProperty("timeout", "30000") + "ms");
            extent.setSystemInfo("Screenshot Capture", config.getProperty("capture.screenshot.on.failure", "true"));
            extent.setSystemInfo("Trace Capture", config.getProperty("capture.trace", "true"));
            extent.setSystemInfo("Maven Surefire Version", System.getProperty("maven.surefire.plugin.version", "3.2.2"));
        }
        
        // Add Playwright-specific information
        extent.setSystemInfo("Playwright Version", "1.40.0");
        extent.setSystemInfo("TestNG Version", "7.8.0");
        extent.setSystemInfo("Extent Reports Version", "5.1.1");
        
        // Add execution environment details
        extent.setSystemInfo("Working Directory", System.getProperty("user.dir"));
        extent.setSystemInfo("Temp Directory", System.getProperty("java.io.tmpdir"));
        extent.setSystemInfo("Timezone", System.getProperty("user.timezone"));
        extent.setSystemInfo("Language", System.getProperty("user.language"));
        extent.setSystemInfo("Country", System.getProperty("user.country"));
    }
    
    @Override
    public void onStart(ITestContext context) {
        // Test suite started - no console output for cleaner reports
    }
    
    @Override
    public void onFinish(ITestContext context) {
        // Generate report
        extent.flush();
        
        // Calculate test statistics (no console output for cleaner reports)
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        
        // Test suite finished - no console output for cleaner reports
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        // Create test in Extent Reports
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(), 
                                                 result.getMethod().getDescription());
        
        // Add test categories
        String[] groups = result.getMethod().getGroups();
        for (String group : groups) {
            extentTest.assignCategory(group);
        }
        
        // Add test parameters
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                extentTest.info("Parameter " + (i + 1) + ": " + parameters[i]);
            }
        }
        
        test.set(extentTest);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.PASS, "Test executed successfully");
            
            // Capture screenshot for successful tests as well
            try {
                Object testInstance = result.getInstance();
                if (testInstance instanceof BaseTest) {
                    BaseTest baseTest = (BaseTest) testInstance;
                    String testName = result.getMethod().getMethodName();
                    String screenshotPath = captureScreenshot(baseTest.getPage(), testName + "_SUCCESS");
                    
                    if (screenshotPath != null) {
                        // Add screenshot with clickable thumbnail for successful tests
                        try {
                            java.io.File screenshotFile = new java.io.File(screenshotPath);
                            if (screenshotFile.exists()) {
                                String base64Image = java.util.Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(screenshotFile.toPath()));
                                extentTest.addScreenCaptureFromPath(screenshotPath, "Screenshot on success");
                                extentTest.info("<div style='margin: 10px 0;'>" +
                                    "<img src='data:image/png;base64," + base64Image + "' " +
                                    "onclick='showScreenshotInModal(\"data:image/png;base64," + base64Image + "\")' " +
                                    "style='width:200px;height:auto;border:2px solid #28a745;border-radius:5px;cursor:pointer;' " +
                                    "class='screenshot-thumb' " +
                                    "alt='Screenshot on success' title='Click to view full size'/>" +
                                    "</div><br/>" +
                                    "<small style='color:#28a745;'>✅ Click thumbnail to view full-size screenshot (opens in same window)</small>");
                            }
                        } catch (Exception e) {
                            extentTest.addScreenCaptureFromPath(screenshotPath, "Screenshot on success");
                        }
                    }
                }
            } catch (Exception e) {
                extentTest.info("Could not capture success screenshot: " + e.getMessage());
            }
            
            // Add execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            extentTest.info("Test execution time: " + duration + " ms");
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
            
            // Add stack trace
            extentTest.fail(result.getThrowable());
            
            // Capture screenshot if test has a page object
            try {
                Object testInstance = result.getInstance();
                if (testInstance instanceof BaseTest) {
                    BaseTest baseTest = (BaseTest) testInstance;
                    String testName = result.getMethod().getMethodName();
                    String screenshotPath = captureScreenshot(baseTest.getPage(), testName);
                    
                    if (screenshotPath != null) {
                        // Add screenshot with both path and base64 for better compatibility
                        try {
                            java.io.File screenshotFile = new java.io.File(screenshotPath);
                            if (screenshotFile.exists()) {
                                String base64Image = java.util.Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(screenshotFile.toPath()));
                                extentTest.addScreenCaptureFromPath(screenshotPath, "Screenshot on failure");
                                extentTest.info("<div style='margin: 10px 0;'>" +
                                    "<img src='data:image/png;base64," + base64Image + "' " +
                                    "onclick='showScreenshotInModal(\"data:image/png;base64," + base64Image + "\")' " +
                                    "style='width:200px;height:auto;border:2px solid #ddd;border-radius:5px;cursor:pointer;' " +
                                    "class='screenshot-thumb' " +
                                    "alt='Screenshot on failure' title='Click to view full size'/>" +
                                    "</div><br/>" +
                                    "<small style='color:#666;'>📸 Click thumbnail to view full-size screenshot (opens in same window)</small>");
                            }
                        } catch (Exception e) {
                            extentTest.addScreenCaptureFromPath(screenshotPath, "Screenshot on failure");
                        }
                    }
                }
            } catch (Exception e) {
                extentTest.info("Could not capture screenshot: " + e.getMessage());
            }
            
            // Add execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            extentTest.info("Test execution time: " + duration + " ms");
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
            
            // Add skip reason
            if (result.getThrowable() != null) {
                extentTest.info("Skip Reason: " + result.getThrowable().getMessage());
            }
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.WARNING, "Test failed but within success percentage");
        }
    }
    
    /**
     * Capture screenshot and return path
     */
    private String captureScreenshot(Page page, String testName) {
        try {
            if (page != null) {
                // Don't set viewport size here - BaseTest already maximizes it
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotPath = Paths.get(SCREENSHOT_DIR, 
                                                testName + "_" + timestamp + ".png").toString();
                
                // Ensure directory exists
                java.io.File screenshotDir = new java.io.File(SCREENSHOT_DIR);
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }
                
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));
                
                // Convert to absolute path for Extent Reports
                return new java.io.File(screenshotPath).getAbsolutePath();
            }
        } catch (Exception e) {
            // Silently handle screenshot errors
        }
        return null;
    }
    
    /**
     * Get current test instance
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Log information to Extent Reports
     */
    public static void logInfo(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.info(message);
        }
    }
    
    /**
     * Log warning to Extent Reports
     */
    public static void logWarning(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.warning(message);
        }
    }
    
    /**
     * Log error to Extent Reports
     */
    public static void logError(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.FAIL, message);
        }
    }
    
    /**
     * Add screenshot to Extent Reports
     */
    public static void addScreenshot(Page page, String title) {
        try {
            if (page != null) {
                // Don't set viewport size here - BaseTest already maximizes it
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotPath = Paths.get(SCREENSHOT_DIR, 
                                                "manual_" + timestamp + ".png").toString();
                
                // Ensure directory exists
                java.io.File screenshotDir = new java.io.File(SCREENSHOT_DIR);
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }
                
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));
                
                ExtentTest extentTest = test.get();
                if (extentTest != null) {
                    // Add screenshot with both path and base64 for better compatibility
                    try {
                        java.io.File screenshotFile = new java.io.File(screenshotPath);
                        if (screenshotFile.exists()) {
                            String absolutePath = screenshotFile.getAbsolutePath();
                            String base64Image = java.util.Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(screenshotFile.toPath()));
                            extentTest.addScreenCaptureFromPath(absolutePath, title);
                            extentTest.info("<div style='margin: 10px 0;'>" +
                                "<img src='data:image/png;base64," + base64Image + "' " +
                                "onclick='showScreenshotInModal(\"data:image/png;base64," + base64Image + "\")' " +
                                "style='width:200px;height:auto;border:2px solid #ddd;border-radius:5px;cursor:pointer;' " +
                                "class='screenshot-thumb' " +
                                "alt='" + title + "' title='Click to view full size'/>" +
                                "</div><br/>" +
                                "<small style='color:#666;'>📸 Click thumbnail to view full-size screenshot (opens in same window)</small>");
                        }
                    } catch (Exception e) {
                        extentTest.addScreenCaptureFromPath(screenshotPath, title);
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle screenshot errors
        }
    }
}
