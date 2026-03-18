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
                        extentTest.addScreenCaptureFromPath(screenshotPath, 
                                                           "Screenshot on failure");
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
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotPath = Paths.get(SCREENSHOT_DIR, 
                                                testName + "_" + timestamp + ".png").toString();
                
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));
                
                return screenshotPath;
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
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotPath = Paths.get(SCREENSHOT_DIR, 
                                                "manual_" + timestamp + ".png").toString();
                
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));
                
                ExtentTest extentTest = test.get();
                if (extentTest != null) {
                    extentTest.addScreenCaptureFromPath(screenshotPath, title);
                }
            }
        } catch (Exception e) {
            // Silently handle screenshot errors
        }
    }
}
