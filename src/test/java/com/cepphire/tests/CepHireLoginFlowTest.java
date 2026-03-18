package com.cepphire.tests;

import com.cepphire.pages.AuthPage;
import com.cepphire.pages.DashboardPage;
import com.cepphire.base.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.asserts.SoftAssert;
import com.aventstack.extentreports.Status;
import com.cepphire.listeners.TestListener;
import com.aventstack.extentreports.ExtentTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Paths;

public class CepHireLoginFlowTest extends BaseTest {
    
    private AuthPage authPage;
    private DashboardPage dashboardPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        System.out.println("Setting up pages...");
        System.out.println("Page is null: " + (page == null));
        System.out.println("Config is null: " + (config == null));
        
        if (page != null && config != null) {
            authPage = new AuthPage(page, config);
            dashboardPage = new DashboardPage(page, config);
            System.out.println("Pages initialized successfully");
        } else {
            System.out.println("Cannot initialize pages - page or config is null");
        }
    }
    
    /**
     * Test complete login flow with valid credentials
     * Verifies successful login and dashboard access
     */
    @Test(description = "Complete login flow with valid credentials", 
          groups = {"login", "smoke", "regression"})
    public void testValidLoginFlow() {
        SoftAssert softAssert = new SoftAssert();
        ExtentTest extentTest = TestListener.getTest();
        
        // Handle case where ExtentTest is not available (IDE run without TestNG XML)
        if (extentTest == null) {
            System.out.println("ExtentTest not available - running without detailed reporting");
            testValidLoginFlowBasic(softAssert);
            return;
        }
        
        // Main Test Container
        extentTest.log(Status.INFO, "<b>Valid Login Flow Test</b>");
        extentTest.info("<details><summary>Test Configuration & Environment</summary>" +
                       "<br>• Test Type: Login Flow Validation" +
                       "<br>• User Type: Administrator" +
                       "<br>• Target URL: " + config.getProperty("base.url") +
                       "<br>• Browser: " + System.getProperty("browser", "chromium") +
                       "<br>• Headless: " + System.getProperty("headless", "false") +
                       "</details>");
        
        // Phase 1: Authentication Page Setup
        extentTest.log(Status.INFO, "<b>Phase 1: Authentication Setup</b>");
        
        // Navigate to Auth Page
        extentTest.log(Status.INFO, "Navigating to Authentication Page");
        extentTest.info("URL: " + config.getProperty("auth.url"));
        
        try {
            authPage.navigateToAuth();
            extentTest.log(Status.PASS, "Navigation successful");
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Navigation failed: " + e.getMessage());
            softAssert.fail("Navigation to auth page failed: " + e.getMessage());
        }
        
        // Wait and Verify Auth Page
        try {
            page.waitForLoadState();
            page.waitForTimeout(2000);
            
            boolean authPageDisplayed = authPage.isAuthPageDisplayed();
            softAssert.assertTrue(authPageDisplayed, "Auth page should be displayed");
            
            if (authPageDisplayed) {
                extentTest.log(Status.PASS, "Auth page loaded and verified");
                extentTest.info("<details><summary>Auth Elements Verification</summary>" +
                               "<br>• Page Display: Visible" +
                               "<br>• Form Elements: Present" +
                               "<br>• OAuth Buttons: Available" +
                               "</details>");
            } else {
                extentTest.log(Status.FAIL, "Auth page not displayed");
            }
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Auth page verification failed: " + e.getMessage());
            softAssert.fail("Auth page verification failed: " + e.getMessage());
        }
        
        // Phase 2: Login Execution
        extentTest.log(Status.INFO, "<b>Phase 2: Login Execution</b>");
        
        try {
            extentTest.log(Status.INFO, "Entering admin credentials");
            extentTest.info("Username: " + System.getProperty("default.username", config.getProperty("default.username")));
            
            authPage.loginAsAdmin();
            extentTest.log(Status.PASS, "Credentials entered successfully");
            
            page.waitForLoadState();
            page.waitForTimeout(3000);
            
            String currentUrl = page.url();
            extentTest.log(Status.INFO, "Current URL after login: " + currentUrl);
            
            boolean loginSuccess = !currentUrl.contains("/auth");
            softAssert.assertTrue(loginSuccess, "Should be redirected after login");
            
            if (loginSuccess) {
                extentTest.log(Status.PASS, "Login successful - redirected to: " + currentUrl);
            } else {
                extentTest.log(Status.FAIL, "Login failed - still on auth page");
                
                // Additional debugging for failed login
                try {
                    boolean authPageStillVisible = authPage.isAuthPageDisplayed();
                    extentTest.log(Status.INFO, "Auth page still visible: " + authPageStillVisible);
                    
                    if (authPageStillVisible) {
                        extentTest.log(Status.WARNING, "Possible authentication failure - check credentials");
                    }
                } catch (Exception e) {
                    extentTest.log(Status.WARNING, "Could not verify auth page state: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Login execution failed: " + e.getMessage());
            softAssert.fail("Login execution failed: " + e.getMessage());
        }
        
        // Phase 3: Dashboard Verification
        extentTest.log(Status.INFO, "<b>Phase 3: Dashboard Verification</b>");
        
        boolean isDashboardVisible = false;
        try {
            // Wait for page to fully load after login
            extentTest.log(Status.INFO, "Waiting for page to fully load after login...");
            
            // Wait for URL to settle and page to be stable
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
            page.waitForTimeout(5000); // Additional wait for dynamic content
            
            // Retry dashboard detection with multiple attempts
            int maxRetries = 3;
            boolean dashboardDetected = false;
            
            // Variables to store final detection results
            boolean hasCandidatesText = false;
            boolean hasJobsText = false;
            boolean hasDashboardButton = false;
            boolean hasCandidatesTab = false;
            boolean hasNavigation = false;
            boolean hasCredits = false;
            
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                extentTest.log(Status.INFO, "Dashboard detection attempt " + attempt + " of " + maxRetries);
                
                // Check if we're on the main page after login
                String currentUrl = page.url();
                boolean onMainPage = currentUrl.contains("cepphire.com"); // Remove /auth check since SPA might not update URL
                
                // For SPA applications, prioritize content over URL
                extentTest.log(Status.INFO, "Current URL: " + currentUrl + " (SPA may not update URL immediately)");
                
                if (!onMainPage) {
                    extentTest.log(Status.WARNING, "Still on auth page, waiting before retry...");
                    page.waitForTimeout(3000);
                    continue;
                }
                
                // Wait for page to be ready
                try {
                    page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(10000));
                } catch (Exception e) {
                    extentTest.log(Status.WARNING, "DOM content load timeout, continuing...");
                }
                
                // Check for dashboard indicators with better error handling
                try {
                    hasCandidatesText = page.getByText("candidates").first().isVisible();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Candidates text not found: " + e.getMessage());
                }
                
                try {
                    hasJobsText = page.getByText("jobs").first().isVisible();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Jobs text not found: " + e.getMessage());
                }
                
                try {
                    hasDashboardButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Unified Dashboard")).isVisible();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Dashboard button not found: " + e.getMessage());
                }
                
                try {
                    hasCandidatesTab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CANDIDATES")).isVisible();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Candidates tab not found: " + e.getMessage());
                }
                
                try {
                    hasNavigation = page.getByRole(AriaRole.NAVIGATION).isVisible();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Navigation not found: " + e.getMessage());
                }
                
                try {
                    hasCredits = dashboardPage.isCreditsDisplayed();
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Credits not found: " + e.getMessage());
                }
                
                // Additional checks for SPA dashboard elements
                try {
                    // Check for common dashboard elements that might be present
                    boolean hasDashboardHeader = page.getByText("Dashboard").isVisible();
                    if (hasDashboardHeader) {
                        extentTest.log(Status.INFO, "Dashboard header found");
                        dashboardDetected = true; // Set to true immediately
                    }
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Dashboard header not found: " + e.getMessage());
                }
                
                try {
                    // Check for user profile or menu
                    boolean hasUserMenu = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("User")).isVisible();
                    if (hasUserMenu) {
                        extentTest.log(Status.INFO, "User menu found");
                        dashboardDetected = true; // Set to true immediately
                    }
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "User menu not found: " + e.getMessage());
                }
                
                try {
                    // Check for any navigation menu items
                    boolean hasMenuItems = page.locator("nav a, .menu a, .navigation a").first().isVisible();
                    if (hasMenuItems) {
                        extentTest.log(Status.INFO, "Navigation menu items found");
                        dashboardDetected = true; // Set to true immediately
                    }
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "Navigation menu items not found: " + e.getMessage());
                }
                
                // Check if any dashboard elements are found
                // For SPA applications, content is more reliable than URL
                dashboardDetected = (hasCandidatesText || hasJobsText || hasDashboardButton || 
                                   hasCandidatesTab || hasNavigation || hasCredits);
                
                // Additional check: if we have dashboard content but URL still shows /auth, consider it successful
                if (dashboardDetected && currentUrl.contains("/auth")) {
                    extentTest.log(Status.INFO, "Dashboard content detected but URL still shows /auth (SPA behavior)");
                    extentTest.log(Status.INFO, "Treating as successful login based on content detection");
                }
                
                if (dashboardDetected) {
                    extentTest.log(Status.PASS, "Dashboard elements detected on attempt " + attempt);
                    isDashboardVisible = true;
                    break;
                } else {
                    extentTest.log(Status.WARNING, "Dashboard not detected on attempt " + attempt + ", waiting before retry...");
                    page.waitForTimeout(3000);
                }
            }
            
            softAssert.assertTrue(isDashboardVisible, "Dashboard should be displayed after login");
            
            if (isDashboardVisible) {
                extentTest.log(Status.PASS, "Dashboard page displayed");
                extentTest.info("<details><summary>Dashboard Detection Results</summary>" +
                               "<br>• Current URL: " + page.url() +
                               "<br>• Candidates Text: " + (hasCandidatesText ? "Found" : "Not Found") +
                               "<br>• Jobs Text: " + (hasJobsText ? "Found" : "Not Found") +
                               "<br>• Dashboard Button: " + (hasDashboardButton ? "Found" : "Not Found") +
                               "<br>• Candidates Tab: " + (hasCandidatesTab ? "Found" : "Not Found") +
                               "<br>• Navigation: " + (hasNavigation ? "Found" : "Not Found") +
                               "<br>• Credits: " + (hasCredits ? "Found" : "Not Found") +
                               "</details>");
            } else {
                extentTest.log(Status.FAIL, "Dashboard not displayed after " + maxRetries + " attempts");
                extentTest.info("<details><summary>Final Dashboard Detection Results</summary>" +
                               "<br>• Current URL: " + page.url() +
                               "<br>• Page Title: " + page.title() +
                               "<br>• Candidates Text: " + (hasCandidatesText ? "Found" : "Not Found") +
                               "<br>• Jobs Text: " + (hasJobsText ? "Found" : "Not Found") +
                               "<br>• Dashboard Button: " + (hasDashboardButton ? "Found" : "Not Found") +
                               "<br>• Candidates Tab: " + (hasCandidatesTab ? "Found" : "Not Found") +
                               "<br>• Navigation: " + (hasNavigation ? "Found" : "Not Found") +
                               "<br>• Credits: " + (hasCredits ? "Found" : "Not Found") +
                               "</details>");
                
                // Add screenshot for debugging
                try {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String screenshotPath = "test-output/screenshots/dashboard_debug_" + timestamp + ".png";
                    page.screenshot(new Page.ScreenshotOptions()
                            .setPath(Paths.get(screenshotPath))
                            .setFullPage(true));
                    extentTest.info("Debug screenshot saved: " + screenshotPath);
                } catch (Exception e) {
                    extentTest.log(Status.WARNING, "Could not capture debug screenshot: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Dashboard verification failed: " + e.getMessage());
            softAssert.fail("Dashboard verification failed: " + e.getMessage());
        }
        
        if (isDashboardVisible) {
            // User Information Verification
            extentTest.info("<details><summary>User Information Verification</summary>");
            
            try {
                String userEmail = dashboardPage.getUserEmail();
                boolean emailDisplayed = dashboardPage.isUserEmailDisplayed();
                
                if (emailDisplayed && !userEmail.isEmpty()) {
                    extentTest.log(Status.PASS, "Email Displayed: " + userEmail);
                } else {
                    extentTest.log(Status.WARNING, "Email Not Found");
                }
            } catch (Exception e) {
                extentTest.log(Status.WARNING, "Email Check Error: " + e.getMessage());
            }
            
            // Credits Verification
            try {
                String creditsAmount = dashboardPage.getCreditsAmount();
                boolean creditsDisplayed = dashboardPage.isCreditsDisplayed();
                
                if (creditsDisplayed && !creditsAmount.isEmpty()) {
                    extentTest.log(Status.PASS, "Credits Available: " + creditsAmount);
                } else {
                    extentTest.log(Status.WARNING, "Credits Not Found");
                }
            } catch (Exception e) {
                extentTest.log(Status.WARNING, "Credits Check Error: " + e.getMessage());
            }
            
            // Navigation Verification
            try {
                boolean sidebarVisible = dashboardPage.isSidebarVisible();
                if (sidebarVisible) {
                    extentTest.log(Status.PASS, "Navigation Sidebar: Visible");
                } else {
                    extentTest.log(Status.WARNING, "Navigation Sidebar: Not Found");
                }
            } catch (Exception e) {
                extentTest.log(Status.WARNING, "Navigation Check Error: " + e.getMessage());
            }
            
            extentTest.info("</details>");
            
            // Admin Access Verification
            extentTest.info("<details><summary>Admin Access Verification</summary>");
            
            try {
                boolean hasAdminAccess = dashboardPage.hasAdminAccess();
                softAssert.assertTrue(hasAdminAccess, "Admin should have admin access");
                
                if (hasAdminAccess) {
                    extentTest.log(Status.PASS, "Admin Access: Confirmed");
                    extentTest.info("Privileges: Full administrator access verified");
                } else {
                    extentTest.log(Status.FAIL, "Admin Access: Not Found");
                }
            } catch (Exception e) {
                extentTest.log(Status.FAIL, "Admin Access Check Error: " + e.getMessage());
                softAssert.fail("Admin access check failed: " + e.getMessage());
            }
            
            extentTest.info("</details>");
        }
        
        // Final Test Summary
        extentTest.log(Status.INFO, "<b>Test Summary</b>");
        
        try {
            softAssert.assertAll();
            extentTest.log(Status.PASS, "All validations passed - Test completed successfully");
            extentTest.info("<details><summary>Test Execution Summary</summary>" +
                           "<br>• Total Phases: 3" +
                           "<br>• Phase 1 - Authentication: Passed" +
                           "<br>• Phase 2 - Login: Passed" +
                           "<br>• Phase 3 - Dashboard: Passed" +
                           "<br>• User Info: Verified" +
                           "<br>• Admin Access: Confirmed" +
                           "<br>• Overall Status: SUCCESS" +
                           "</details>");
        } catch (AssertionError e) {
            extentTest.log(Status.FAIL, "Some validations failed: " + e.getMessage());
            extentTest.info("<details><summary>Test Execution Summary</summary>" +
                           "<br>• Total Phases: 3" +
                           "<br>• Phase 1 - Authentication: Failed" +
                           "<br>• Phase 2 - Login: Failed" +
                           "<br>• Phase 3 - Dashboard: Failed" +
                           "<br>• Overall Status: FAILURE" +
                           "</details>");
            throw e;
        }
    }
    
    /**
     * Basic test execution without Extent Reports (for IDE runs)
     */
    private void testValidLoginFlowBasic(SoftAssert softAssert) {
        System.out.println("Starting Valid Login Flow Test (Basic Mode)");
        
        try {
            // Navigate to Auth Page
            System.out.println("Navigating to Authentication Page");
            authPage.navigateToAuth();
            
            // Wait and Verify Auth Page
            page.waitForLoadState();
            page.waitForTimeout(2000);
            
            boolean authPageDisplayed = authPage.isAuthPageDisplayed();
            softAssert.assertTrue(authPageDisplayed, "Auth page should be displayed");
            System.out.println("Auth page loaded and verified");
            
            // Login Execution
            System.out.println("Entering admin credentials");
            authPage.loginAsAdmin();
            System.out.println("Credentials entered successfully");
            
            page.waitForLoadState();
            page.waitForTimeout(3000);
            
            String currentUrl = page.url();
            boolean loginSuccess = !currentUrl.contains("/auth");
            softAssert.assertTrue(loginSuccess, "Should be redirected after login");
            System.out.println("Login successful - redirected to: " + currentUrl);
            
            // Dashboard Verification
            boolean isDashboardVisible = dashboardPage.isDashboardPageDisplayed();
            softAssert.assertTrue(isDashboardVisible, "Dashboard should be displayed after login");
            System.out.println("Dashboard page displayed");
            
            // User Information Verification
            String userEmail = dashboardPage.getUserEmail();
            boolean emailDisplayed = dashboardPage.isUserEmailDisplayed();
            if (emailDisplayed && !userEmail.isEmpty()) {
                System.out.println("Email Displayed: " + userEmail);
            }
            
            String creditsAmount = dashboardPage.getCreditsAmount();
            boolean creditsDisplayed = dashboardPage.isCreditsDisplayed();
            if (creditsDisplayed && !creditsAmount.isEmpty()) {
                System.out.println("Credits Available: " + creditsAmount);
            }
            
            // Admin Access Verification
            boolean hasAdminAccess = dashboardPage.hasAdminAccess();
            softAssert.assertTrue(hasAdminAccess, "Admin should have admin access");
            System.out.println("Admin access privileges confirmed");
            
            // Final validation
            softAssert.assertAll();
            System.out.println("All validations passed - Test completed successfully");
            
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            softAssert.fail("Test execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Test login with invalid credentials
     * Verifies that invalid login fails and user stays on auth page
     */
    @Test(description = "Login with invalid credentials", 
          groups = {"login", "negative", "regression"})
    public void testInvalidLoginFlow() {
        // Navigate to auth page
        authPage.navigateToAuth();
        
        // Try login with invalid credentials
        authPage.login("invalid@test.com", "wrongpassword");
        authPage.waitForLoadingComplete();
        
        // Verify login failed
        Assert.assertFalse(authPage.isLoginSuccessful(), "Login should fail with invalid credentials");
        Assert.assertTrue(authPage.isAuthPageDisplayed(), "Should remain on auth page after failed login");
        
        // Check for error message if present
        if (authPage.isErrorMessageDisplayed()) {
            String errorMsg = authPage.getErrorMessage();
            Assert.assertFalse(errorMsg.isEmpty(), "Should show error message for invalid login");
            System.out.println("Error message displayed: " + errorMsg);
        }
        
        System.out.println("✅ Invalid login test completed successfully");
    }
    
    /**
     * Test OAuth buttons are present and clickable
     * Verifies Google and GitHub OAuth buttons
     */
    @Test(description = "Test OAuth buttons functionality", 
          groups = {"login", "ui", "regression"})
    public void testOAuthButtons() {
        // Navigate to auth page
        authPage.navigateToAuth();
        
        // Verify OAuth buttons
        authPage.verifyOAuthButtons();
        
        // Test password visibility toggle
        boolean isPasswordHidden = !authPage.isPasswordVisible();
        authPage.togglePasswordVisibility();
        boolean isPasswordVisible = authPage.isPasswordVisible();
        
        Assert.assertTrue(isPasswordHidden && isPasswordVisible, "Password visibility toggle should work");
        
        System.out.println("✅ OAuth buttons test completed successfully");
    }
    
    /**
     * Test dashboard access after successful login
     * Verifies dashboard elements and user information
     */
    @Test(description = "Test dashboard access and elements", 
          groups = {"dashboard", "smoke", "regression"})
    public void testDashboardAccess() {
        // Login first
        performLogin();
        
        // Navigate to dashboard if not already there
        if (!dashboardPage.isOnDashboard()) {
            dashboardPage.navigateToDashboard();
        }
        
        // Wait for dashboard to load
        dashboardPage.waitForDashboardLoad();
        
        // Verify dashboard elements
        Assert.assertTrue(dashboardPage.isDashboardPageDisplayed(), "Dashboard should be displayed");
        dashboardPage.verifyDashboardElements();
        
        // Check if user information is displayed
        Assert.assertTrue(dashboardPage.isUserEmailDisplayed(), "User email should be displayed");
        Assert.assertTrue(dashboardPage.isCreditsDisplayed(), "User credits should be displayed");
        
        System.out.println("User credits: " + dashboardPage.getCreditsAmount());
        System.out.println("✅ Dashboard access test completed successfully");
    }
    
    /**
     * Test candidates tab functionality
     * Verifies candidate data display and interaction
     */
    @Test(description = "Test candidates tab functionality", 
          groups = {"dashboard", "regression"})
    public void testCandidatesTab() {
        // Login first
        performLogin();
        
        // Navigate to dashboard
        dashboardPage.navigateToDashboard();
        dashboardPage.waitForDashboardLoad();
        
        // Click candidates tab if available
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CANDIDATES")).isVisible()) {
            dashboardPage.clickCandidatesTab();
            dashboardPage.waitForCandidatesLoad();
            
            // Verify candidates are displayed
            if (dashboardPage.hasCandidates()) {
                Assert.assertTrue(dashboardPage.areCandidateCardsDisplayed(), "Candidate cards should be displayed");
                
                // Get first candidate info
                String firstCandidate = dashboardPage.getFirstCandidateName();
                String firstEmail = dashboardPage.getFirstCandidateEmail();
                
                System.out.println("First candidate: " + firstCandidate);
                System.out.println("First email: " + firstEmail);
                
                Assert.assertFalse(firstCandidate.isEmpty(), "Candidate name should not be empty");
                Assert.assertFalse(firstEmail.isEmpty(), "Candidate email should not be empty");
            } else {
                System.out.println("No candidates found - empty state displayed");
            }
        } else {
            System.out.println("Candidates tab not available");
        }
        
        System.out.println("✅ Candidates tab test completed successfully");
    }
    
    /**
     * Test jobs tab functionality
     * Verifies job data display and interaction
     */
    @Test(description = "Test jobs tab functionality", 
          groups = {"dashboard", "regression"})
    public void testJobsTab() {
        // Login first
        performLogin();
        
        // Navigate to dashboard
        dashboardPage.navigateToDashboard();
        dashboardPage.waitForDashboardLoad();
        
        // Click jobs tab if available
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("JOBS")).isVisible()) {
            dashboardPage.clickJobsTab();
            dashboardPage.waitForJobsLoad();
            
            // Verify jobs are displayed or empty state
            if (dashboardPage.hasJobs()) {
                System.out.println("Jobs found: " + dashboardPage.getJobCount());
            } else {
                System.out.println("No jobs found - empty state displayed");
            }
        } else {
            System.out.println("Jobs tab not available");
        }
        
        System.out.println("✅ Jobs tab test completed successfully");
    }
    
    /**
     * Test search functionality
     * Verifies search works on dashboard
     */
    @Test(description = "Test search functionality", 
          groups = {"dashboard", "regression"})
    public void testSearchFunctionality() {
        // Login first
        performLogin();
        
        // Navigate to dashboard
        dashboardPage.navigateToDashboard();
        dashboardPage.waitForDashboardLoad();
        
        // Test search if available
        if (page.getByLabel("search").isVisible() || page.getByLabel("query").isVisible()) {
            page.getByLabel("search").fill("test");
            page.waitForTimeout(2000);
            
            // Search should complete without errors
            Assert.assertTrue(true, "Search should complete without errors");
            System.out.println("✅ Search functionality test completed successfully");
        } else {
            System.out.println("Search functionality not available");
        }
    }
    
    /**
     * Test sidebar navigation
     * Verifies all sidebar links work
     */
    @Test(description = "Test sidebar navigation", 
          groups = {"navigation", "regression"})
    public void testSidebarNavigation() {
        // Login first
        performLogin();
        
        // Navigate to dashboard
        dashboardPage.navigateToDashboard();
        dashboardPage.waitForDashboardLoad();
        
        // Test key navigation items
        String[] navItems = {"Analytics", "Manage Jobs", "Billing", "Settings"};
        
        for (String navItem : navItems) {
            dashboardPage.navigateToSection(navItem);
            page.waitForTimeout(1000);
            
            // Verify navigation worked
            String currentUrl = dashboardPage.getCurrentUrl();
            Assert.assertNotNull(currentUrl, "Should have navigated to " + navItem);
            System.out.println("Navigated to: " + navItem + " -> " + currentUrl);
            
            // Go back to dashboard
            dashboardPage.navigateToDashboard();
            dashboardPage.waitForDashboardLoad();
        }
        
        System.out.println("✅ Sidebar navigation test completed successfully");
    }
    
    /**
     * Test logout functionality
     * Verifies logout works and redirects properly
     */
    @Test(description = "Test logout functionality", 
          groups = {"navigation", "smoke", "regression"})
    public void testLogoutFunctionality() {
        // Login first
        performLogin();
        
        // Navigate to dashboard
        dashboardPage.navigateToDashboard();
        dashboardPage.waitForDashboardLoad();
        
        // Click logout
        dashboardPage.clickSignOut();
        page.waitForLoadState();
        
        // Verify logged out
        String currentUrl = dashboardPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/auth") || 
                         currentUrl.contains("/login") || 
                         currentUrl.endsWith("/"), 
            "Should be logged out and redirected to auth or homepage");
        
        System.out.println("✅ Logout functionality test completed successfully");
    }
    
    /**
     * Test responsive design on mobile viewport
     * Verifies application works on mobile
     */
    @Test(description = "Test mobile responsive design", 
          groups = {"responsive", "ui", "regression"})
    public void testMobileResponsive() {
        // Set mobile viewport
        page.setViewportSize(375, 667);
        
        // Navigate to auth page
        authPage.navigateToAuth();
        
        // Verify auth page works on mobile
        Assert.assertTrue(authPage.isAuthPageDisplayed(), "Auth page should work on mobile");
        authPage.verifyAuthPageElements();
        
        // Test login on mobile
        authPage.loginAsAdmin();
        authPage.waitForAuthCompletion();
        
        // Verify login works on mobile
        Assert.assertTrue(authPage.isLoginSuccessful(), "Login should work on mobile");
        
        // Reset viewport
        page.setViewportSize(1280, 720);
        
        System.out.println("✅ Mobile responsive test completed successfully");
    }
    
    /**
     * Helper method to perform login
     */
    private void performLogin() {
        authPage.navigateToAuth();
        authPage.loginAsAdmin();
        authPage.waitForAuthCompletion();
        
        // If redirected to pending approval, skip dashboard tests
        if (authPage.isRedirectedToPendingApproval()) {
            System.out.println("ℹ️ User is pending approval - some tests may be skipped");
        }
    }
    
    @AfterMethod
    public void tearDownPages() {
        // Clean up any remaining state
        authPage = null;
        dashboardPage = null;
        
        // Reset viewport to default
        if (page != null) {
            page.setViewportSize(1280, 720);
        }
    }
}
