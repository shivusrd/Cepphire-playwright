package com.cepphire.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class TestUtils {
    
    private static final Random random = new Random();
    
    /**
     * Wait for element to be visible with custom timeout
     */
    public static void waitForElementVisible(Page page, String selector, int timeoutMs) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }
    
    /**
     * Wait for element to be hidden
     */
    public static void waitForElementHidden(Page page, String selector, int timeoutMs) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                .setTimeout(timeoutMs));
    }
    
    /**
     * Wait for element to be enabled
     */
    public static void waitForElementEnabled(Page page, String selector, int timeoutMs) {
        Locator element = page.locator(selector);
        element.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
        
        // Additional wait for element to be enabled
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (element.isEnabled()) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Generate random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generate random email
     */
    public static String generateRandomEmail() {
        return "testuser" + generateRandomString(8) + "@example.com";
    }
    
    /**
     * Generate random phone number
     */
    public static String generateRandomPhoneNumber() {
        return String.format("%03d%03d%04d", 
            random.nextInt(900) + 100, 
            random.nextInt(900) + 100, 
            random.nextInt(10000));
    }
    
    /**
     * Scroll element into view
     */
    public static void scrollIntoView(Page page, String selector) {
        page.locator(selector).scrollIntoViewIfNeeded();
    }
    
    /**
     * Click element with JavaScript (for elements that are hard to click normally)
     */
    public static void clickWithJS(Page page, String selector) {
        page.evaluate("document.querySelector('" + selector + "').click()");
    }
    
    /**
     * Hover over element
     */
    public static void hover(Page page, String selector) {
        page.locator(selector).hover();
    }
    
    /**
     * Double click element
     */
    public static void doubleClick(Page page, String selector) {
        page.locator(selector).dblclick();
    }
    
    /**
     * Right click element
     */
    public static void rightClick(Page page, String selector) {
        page.locator(selector).click(new Locator.ClickOptions().setButton(com.microsoft.playwright.options.MouseButton.RIGHT));
    }
    
    /**
     * Get all text content from elements matching selector
     */
    public static List<String> getAllText(Page page, String selector) {
        return page.locator(selector).allInnerTexts();
    }
    
    /**
     * Count elements matching selector
     */
    public static int countElements(Page page, String selector) {
        return page.locator(selector).count();
    }
    
    /**
     * Check if element exists
     */
    public static boolean elementExists(Page page, String selector) {
        return page.locator(selector).count() > 0;
    }
    
    /**
     * Get attribute value
     */
    public static String getAttribute(Page page, String selector, String attributeName) {
        return page.locator(selector).getAttribute(attributeName);
    }
    
    /**
     * Get CSS property value
     */
    public static String getCssProperty(Page page, String selector, String propertyName) {
        return page.locator(selector).evaluate("el => window.getComputedStyle(el).getPropertyValue('" + propertyName + "')").toString();
    }
    
    /**
     * Wait for page load to complete
     */
    public static void waitForPageLoad(Page page, int timeoutMs) {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
    }
    
    /**
     * Wait for network idle
     */
    public static void waitForNetworkIdle(Page page, int timeoutMs) {
        page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
    }
    
    /**
     * Take screenshot with custom name
     */
    public static void takeScreenshot(Page page, String fileName) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("test-output/screenshots/" + fileName + ".png"))
                .setFullPage(true));
    }
    
    /**
     * Verify element text contains expected text (case-insensitive)
     */
    public static void verifyTextContains(Page page, String selector, String expectedText) {
        String actualText = page.locator(selector).textContent().trim();
        Assert.assertTrue(actualText.toLowerCase().contains(expectedText.toLowerCase()),
            "Expected text '" + expectedText + "' not found in actual text '" + actualText + "'");
    }
    
    /**
     * Verify element text equals expected text (case-insensitive)
     */
    public static void verifyTextEquals(Page page, String selector, String expectedText) {
        String actualText = page.locator(selector).textContent().trim();
        Assert.assertEquals(actualText.toLowerCase(), expectedText.toLowerCase(),
            "Expected text '" + expectedText + "' does not match actual text '" + actualText + "'");
    }
    
    /**
     * Verify element is visible
     */
    public static void verifyElementVisible(Page page, String selector) {
        Locator element = page.locator(selector);
        Assert.assertTrue(element.isVisible(), "Element '" + selector + "' should be visible");
    }
    
    /**
     * Verify element is not visible
     */
    public static void verifyElementNotVisible(Page page, String selector) {
        Locator element = page.locator(selector);
        Assert.assertFalse(element.isVisible(), "Element '" + selector + "' should not be visible");
    }
    
    /**
     * Verify element is enabled
     */
    public static void verifyElementEnabled(Page page, String selector) {
        Locator element = page.locator(selector);
        Assert.assertTrue(element.isEnabled(), "Element '" + selector + "' should be enabled");
    }
    
    /**
     * Verify element is disabled
     */
    public static void verifyElementDisabled(Page page, String selector) {
        Locator element = page.locator(selector);
        Assert.assertFalse(element.isEnabled(), "Element '" + selector + "' should be disabled");
    }
    
    /**
     * Verify URL contains expected text
     */
    public static void verifyUrlContains(Page page, String expectedUrlText) {
        String currentUrl = page.url();
        Assert.assertTrue(currentUrl.contains(expectedUrlText),
            "URL '" + currentUrl + "' should contain '" + expectedUrlText + "'");
    }
    
    /**
     * Verify page title contains expected text
     */
    public static void verifyTitleContains(Page page, String expectedTitleText) {
        String title = page.title();
        Assert.assertTrue(title.toLowerCase().contains(expectedTitleText.toLowerCase()),
            "Page title '" + title + "' should contain '" + expectedTitleText + "'");
    }
    
    /**
     * Clear input field and type text
     */
    public static void clearAndType(Page page, String selector, String text) {
        page.locator(selector).clear();
        page.locator(selector).fill(text);
    }
    
    /**
     * Select dropdown option by visible text
     */
    public static void selectDropdownByText(Page page, String selector, String visibleText) {
        page.selectOption(selector, new com.microsoft.playwright.options.SelectOption().setLabel(visibleText));
    }
    
    /**
     * Select dropdown option by value
     */
    public static void selectDropdownByValue(Page page, String selector, String value) {
        page.selectOption(selector, new com.microsoft.playwright.options.SelectOption().setValue(value));
    }
    
    /**
     * Get selected dropdown option text
     */
    public static String getSelectedDropdownText(Page page, String selector) {
        return page.locator(selector + " option:checked").textContent();
    }
    
    /**
     * Check if checkbox is checked
     */
    public static boolean isCheckboxChecked(Page page, String selector) {
        return page.locator(selector).isChecked();
    }
    
    /**
     * Check checkbox
     */
    public static void checkCheckbox(Page page, String selector) {
        page.locator(selector).check();
    }
    
    /**
     * Uncheck checkbox
     */
    public static void uncheckCheckbox(Page page, String selector) {
        page.locator(selector).uncheck();
    }
    
    /**
     * Switch to iframe by selector
     */
    public static void switchToIframe(Page page, String iframeSelector) {
        // Note: In Playwright, you work with frames directly rather than switching
        // Use page.frameLocator(iframeSelector) to work with iframe content
        System.out.println("Frame switching in Playwright uses frameLocator() method");
    }
    
    /**
     * Execute JavaScript and return result
     */
    public static Object executeJavaScript(Page page, String script) {
        return page.evaluate(script);
    }
    
    /**
     * Wait for JavaScript condition to be true
     */
    public static void waitForJavaScriptCondition(Page page, String jsCondition, int timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            Boolean result = (Boolean) page.evaluate(jsCondition);
            if (result != null && result) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        Assert.fail("JavaScript condition not met within timeout: " + jsCondition);
    }
    
    /**
     * Generate timestamp for unique test data
     */
    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Pause execution for specified milliseconds
     */
    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Format string with test data
     */
    public static String formatTestData(String template, String... replacements) {
        String result = template;
        for (int i = 0; i < replacements.length; i++) {
            result = result.replace("{" + i + "}", replacements[i]);
        }
        return result;
    }
}
