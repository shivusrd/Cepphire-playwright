package com.cepphire.base;

import com.cepphire.utils.ConfigReader;
import com.cepphire.utils.JsonDataReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class BaseTest {
    
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected Properties config;
    protected JsonDataReader testData;
    
    protected static final String SCREENSHOT_DIR = "test-output/screenshots";
    protected static final String TRACE_DIR = "test-output/traces";
    
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chromium") String browserName, @Optional("false") String headless) throws IOException {
        // Load configuration
        config = ConfigReader.getProperties();
        testData = new JsonDataReader(config.getProperty("test.data.file.path"));
        
        // Get system properties from GitHub Actions with fallbacks
        String systemBrowser = System.getProperty("browser", browserName);
        String systemHeadless = System.getProperty("headless", headless);
        String systemBaseUrl = System.getProperty("base.url", config.getProperty("base.url"));
        
        // Auto-detect headless mode for CI/CD environments
        if (systemHeadless.equals("false")) {
            // Check if we're in a headless environment (GitHub Actions, Jenkins, etc.)
            String ciEnvironment = System.getenv("CI");
            String githubActions = System.getenv("GITHUB_ACTIONS");
            
            System.out.println("Environment Detection Debug:");
            System.out.println("  CI env: " + ciEnvironment);
            System.out.println("  GITHUB_ACTIONS env: " + githubActions);
            System.out.println("  Original headless setting: " + systemHeadless);
            
            // Only force headless mode in actual CI environments
            if (ciEnvironment != null || githubActions != null) {
                System.out.println("CI/CD environment detected, forcing headless mode");
                systemHeadless = "true";
            } else {
                System.out.println("Local environment detected, keeping headless=false");
            }
        } else {
            System.out.println("Headless mode already set to: " + systemHeadless);
        }
        
        // Initialize Playwright
        playwright = Playwright.create();
        
        // Browser configuration - prioritize system properties
        BrowserType browserType = getBrowserType(systemBrowser);
        boolean isHeadless = Boolean.parseBoolean(systemHeadless);
        
        // Adjust slowMo based on headless mode - slower for headless to allow proper rendering
        int slowMoTime = isHeadless ? 200 : 100; // Slower in headless mode
        
        browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(slowMoTime));
        
        // Browser context configuration - handle headless vs headful
        int screenWidth, screenHeight;
        
        if (Boolean.parseBoolean(systemHeadless)) {
            // Use default desktop resolution for headless mode
            screenWidth = 1920;
            screenHeight = 1080;
            System.out.println("Using default desktop resolution for headless mode: " + screenWidth + "x" + screenHeight);
        } else {
            // Get actual screen size for headful mode - only if not headless
            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                screenWidth = (int) screenSize.getWidth();
                screenHeight = (int) screenSize.getHeight();
                System.out.println("Using actual screen resolution: " + screenWidth + "x" + screenHeight);
            } catch (Exception e) {
                // Fallback to default if screen detection fails
                screenWidth = 1920;
                screenHeight = 1080;
                System.out.println("Screen detection failed, using default resolution: " + screenWidth + "x" + screenHeight);
            }
        }
        
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(screenWidth, screenHeight)
                .setIgnoreHTTPSErrors(true)
                .setAcceptDownloads(true));
        
        // Page configuration
        page = context.newPage();
        page.setDefaultTimeout(Double.parseDouble(config.getProperty("timeout", "30000")));
        
        // Start tracing if enabled
        if (Boolean.parseBoolean(config.getProperty("capture.trace", "true"))) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
        
        // Navigate to base URL - prioritize system property
        page.navigate(systemBaseUrl);
        
        // Log configuration for debugging
        System.out.println("Browser Configuration:");
        System.out.println("  Browser: " + systemBrowser);
        System.out.println("  Headless: " + systemHeadless);
        System.out.println("  Base URL: " + systemBaseUrl);
        System.out.println("  Screen Resolution: " + screenWidth + "x" + screenHeight);
        System.out.println("  Environment: " + (System.getenv("GITHUB_ACTIONS") != null ? "GitHub Actions" : "Local"));
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            // Capture trace if enabled
            if (Boolean.parseBoolean(config.getProperty("capture.trace", "true")) && context != null) {
                String tracePath = Paths.get(TRACE_DIR, "trace-" + System.currentTimeMillis() + ".zip").toString();
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get(tracePath)));
            }
        } catch (Exception e) {
            System.err.println("Error saving trace: " + e.getMessage());
        }
        
        // Clean up resources
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    private BrowserType getBrowserType(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chromium":
                return playwright.chromium();
            case "firefox":
                return playwright.firefox();
            case "webkit":
                return playwright.webkit();
            default:
                return playwright.chromium();
        }
    }
    
    /**
     * Capture screenshot on failure
     */
    protected void captureScreenshot(String testName) {
        try {
            if (page != null) {
                String screenshotPath = Paths.get(SCREENSHOT_DIR, testName + "-" + System.currentTimeMillis() + ".png").toString();
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));
                System.out.println("Screenshot saved: " + screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Wait for element to be visible
     */
    protected void waitForElement(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Wait for element to be clickable
     */
    protected void waitForElementToBeClickable(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        Locator element = page.locator(selector);
        element.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }
    
    /**
     * Get the Playwright Page instance
     */
    public Page getPage() {
        return page;
    }
    
    /**
     * Navigate to specific URL
     */
    protected void navigateTo(String url) {
        page.navigate(config.getProperty("base.url") + url);
    }
    
    /**
     * Check if element is visible
     */
    protected boolean isElementVisible(String selector) {
        try {
            Locator element = page.locator(selector);
            return element.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get text from element
     */
    protected String getElementText(String selector) {
        try {
            Locator element = page.locator(selector);
            return element.textContent() != null ? element.textContent().trim() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
