package com.cepphire.pages;

import com.cepphire.base.BaseTest;
import com.cepphire.utils.JsonDataReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;
import java.io.IOException;
import java.util.Properties;

public class AuthPage {
    
    private final Page page;
    private final Properties config;
    private final JsonDataReader testData;
    
    public AuthPage(Page page, Properties config) {
        this.page = page;
        this.config = config;
        // Initialize testData later if needed
        this.testData = null;
    }
    
    /**
     * Navigate to auth page
     */
    public void navigateToAuth() {
        page.navigate(config.getProperty("auth.url"));
        waitForPageLoad();
    }
    
    /**
     * Check if auth page is displayed
     */
    public boolean isAuthPageDisplayed() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sign in to CeppHire")).isVisible();
    }
    
    /**
     * Enter email using semantic locator with fallback
     */
    public void enterEmail(String email) {
        if (page.getByLabel("Email").isVisible()) {
            page.getByLabel("Email").fill(email);
        } else if (page.getByLabel("Corporate Email").isVisible()) {
            page.getByLabel("Corporate Email").fill(email);
        } else {
            page.locator("input[type='email']").fill(email);
        }
    }
    
    /**
     * Enter password using semantic locator with fallback
     */
    public void enterPassword(String password) {
        if (page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).isVisible()) {
            page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(password);
        } else {
            page.locator("input[type='password']").fill(password);
        }
    }
    
    /**
     * Toggle password visibility
     */
    public void togglePasswordVisibility() {
        page.getByLabel("password").locator("button").click();
    }
    
    /**
     * Check if password is visible
     */
    public boolean isPasswordVisible() {
        String inputType = page.getByLabel("Password").getAttribute("type");
        return "text".equals(inputType);
    }
    
    /**
     * Click Access Workspace button
     */
    public void clickAccessWorkspace() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Access Workspace")).click();
    }
    
    /**
     * Click Google OAuth button
     */
    public void clickGoogleSignIn() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Google")).click();
    }
    
    /**
     * Click GitHub OAuth button
     */
    public void clickGitHubSignIn() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("GitHub")).click();
    }
    
    /**
     * Click Request Access button
     */
    public void clickRequestAccess() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Request Access")).click();
    }
    
    /**
     * Perform login with credentials
     */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickAccessWorkspace();
    }
    
    /**
     * Login with admin credentials
     */
    public void loginAsAdmin() {
        // Get credentials from system properties (GitHub Actions) or fallback to config
        String email = System.getProperty("default.username", config.getProperty("default.username"));
        String password = System.getProperty("default.password", config.getProperty("default.password"));
        
        System.out.println("Login attempt with email: " + email);
        System.out.println("Auth URL: " + config.getProperty("auth.url"));
        
        login(email, password);
    }
    
    /**
     * Wait for page to load
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        page.waitForTimeout(2000); // Simple wait for page to be ready
    }
    
    /**
     * Wait for authentication to complete
     */
    public void waitForAuthCompletion() {
        page.waitForLoadState();
        page.waitForTimeout(2000); // Additional wait for redirect
    }
    
    /**
     * Check if login was successful
     */
    public boolean isLoginSuccessful() {
        String currentUrl = page.url();
        return !currentUrl.contains("/auth") && 
               (currentUrl.contains("/dashboard") || 
                currentUrl.contains("/pending-approval") ||
                currentUrl.contains("/candidate-dashboard") ||
                currentUrl.endsWith("/"));
    }
    
    /**
     * Check if redirected to pending approval
     */
    public boolean isRedirectedToPendingApproval() {
        return page.url().contains("/pending-approval");
    }
    
    /**
     * Check if redirected to dashboard
     */
    public boolean isRedirectedToDashboard() {
        return page.url().contains("/dashboard");
    }
    
    /**
     * Check if redirected to candidate dashboard
     */
    public boolean isRedirectedToCandidateDashboard() {
        return page.url().contains("/candidate-dashboard");
    }
    
    /**
     * Verify auth page elements are present
     */
    public void verifyAuthPageElements() {
        // Try semantic locators first, then fallback to CSS selectors
        boolean emailVisible = page.getByLabel("Email").isVisible() || 
                             page.getByLabel("Corporate Email").isVisible() ||
                             page.locator("input[type='email']").isVisible();
        
        boolean passwordVisible = page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).isVisible() ||
                                page.locator("input[type='password']").isVisible();
        
        Assert.assertTrue(emailVisible, "Email input should be visible");
        Assert.assertTrue(passwordVisible, "Password input should be visible");
        
        // Check for submit button
        boolean submitVisible = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Access Workspace")).isVisible() ||
                              page.locator("button[type='submit']").isVisible() ||
                              page.getByText("Access Workspace").isVisible();
        Assert.assertTrue(submitVisible, "Submit button should be visible");
        
        // Check for OAuth buttons
        boolean googleVisible = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Google")).isVisible() ||
                              page.getByText("Google").isVisible();
        boolean githubVisible = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("GitHub")).isVisible() ||
                              page.getByText("GitHub").isVisible();
        
        Assert.assertTrue(googleVisible, "Google OAuth button should be visible");
        Assert.assertTrue(githubVisible, "GitHub OAuth button should be visible");
    }
    
    /**
     * Verify OAuth buttons are working
     */
    public void verifyOAuthButtons() {
        Assert.assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Google")).isEnabled(), "Google button should be enabled");
        Assert.assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("GitHub")).isEnabled(), "GitHub button should be enabled");
    }
    
    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return page.getByRole(AriaRole.ALERT).isVisible();
    }
    
    /**
     * Get error message text
     */
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return page.getByRole(AriaRole.ALERT).textContent().trim();
        }
        return "";
    }
    
    /**
     * Check if loading spinner is visible
     */
    public boolean isLoading() {
        return page.locator(".animate-spin").isVisible();
    }
    
    /**
     * Wait for loading to complete
     */
    public void waitForLoadingComplete() {
        if (isLoading()) {
            page.locator(".animate-spin").waitFor(new Locator.WaitForOptions().setTimeout(15000));
        }
    }
    
    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }
}
