package com.cepphire.base;

import com.cepphire.utils.ConfigReader;
import com.cepphire.utils.JsonDataReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

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
        
        // Initialize Playwright
        playwright = Playwright.create();
        
        // Browser configuration
        BrowserType browserType = getBrowserType(browserName);
        boolean isHeadless = headless != null && Boolean.parseBoolean(headless);
        
        browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(100));
        
        // Browser context configuration
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(
                        Integer.parseInt(config.getProperty("viewport.width", "1280")),
                        Integer.parseInt(config.getProperty("viewport.height", "720"))
                )
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
        
        // Navigate to base URL
        page.navigate(config.getProperty("base.url"));
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
