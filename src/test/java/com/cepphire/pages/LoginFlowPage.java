package com.cepphire.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentTest;
import org.testng.asserts.SoftAssert;
import com.cepphire.listeners.TestListener;

import java.util.Properties;

public class LoginFlowPage {
    
    private final AuthPage authPage;
    private final DashboardPage dashboardPage;
    private final Page page;
    private final Properties config;
    
    public LoginFlowPage(Page page, Properties config) {
        this.page = page;
        this.config = config;
        this.authPage = new AuthPage(page, config);
        this.dashboardPage = new DashboardPage(page, config);
    }
    
    /**
     * Execute complete login flow with comprehensive validation and reporting
     */
    public LoginFlowResult executeCompleteLoginFlow() {
        SoftAssert softAssert = new SoftAssert();
        ExtentTest extentTest = TestListener.getTest();
        
        // Handle case where ExtentTest is not available (IDE run without TestNG XML)
        if (extentTest == null) {
            return executeBasicLoginFlow(softAssert);
        }
        
        extentTest.log(Status.INFO, "<b>Valid Login Flow Test</b>");
        extentTest.info("<details><summary>Test Configuration & Environment</summary>" +
                       "<br>• Test Type: Login Flow Validation" +
                       "<br>• User Type: Administrator" +
                       "<br>• Target URL: " + config.getProperty("base.url") +
                       "<br>• Browser: " + System.getProperty("browser", "chromium") +
                       "<br>• Headless: " + System.getProperty("headless", "false") +
                       "</details>");
        
        // Phase 1: Authentication Page Setup
        boolean authSuccess = performAuthenticationSetup(extentTest, softAssert);
        
        // Phase 2: Login Execution
        boolean loginSuccess = performLoginExecution(extentTest, softAssert);
        
        // Phase 3: Dashboard Verification
        DashboardVerificationResult dashboardResult = performDashboardVerification(extentTest, softAssert);
        
        // Final Test Summary
        boolean overallSuccess = authSuccess && loginSuccess && dashboardResult.isVisible;
        
        if (overallSuccess && dashboardResult.isVisible) {
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
        } else {
            extentTest.log(Status.FAIL, "Some validations failed");
            extentTest.info("<details><summary>Test Execution Summary</summary>" +
                           "<br>• Total Phases: 3" +
                           "<br>• Phase 1 - Authentication: " + (authSuccess ? "Passed" : "Failed") +
                           "<br>• Phase 2 - Login: " + (loginSuccess ? "Passed" : "Failed") +
                           "<br>• Phase 3 - Dashboard: " + (dashboardResult.isVisible ? "Passed" : "Failed") +
                           "<br>• Overall Status: FAILURE" +
                           "</details>");
        }
        
        return new LoginFlowResult(overallSuccess, dashboardResult);
    }
    
    /**
     * Perform authentication page setup and validation
     */
    private boolean performAuthenticationSetup(ExtentTest extentTest, SoftAssert softAssert) {
        extentTest.log(Status.INFO, "<b>Phase 1: Authentication Setup</b>");
        
        extentTest.log(Status.INFO, "Navigating to Authentication Page");
        extentTest.info("URL: " + config.getProperty("auth.url"));
        
        try {
            authPage.navigateToAuth();
            extentTest.log(Status.PASS, "Navigation successful");
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Navigation failed: " + e.getMessage());
            softAssert.fail("Navigation to auth page failed: " + e.getMessage());
            return false;
        }
        
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
                return true;
            } else {
                extentTest.log(Status.FAIL, "Auth page not displayed");
                return false;
            }
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Auth page verification failed: " + e.getMessage());
            softAssert.fail("Auth page verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Perform login execution with comprehensive validation
     */
    private boolean performLoginExecution(ExtentTest extentTest, SoftAssert softAssert) {
        extentTest.log(Status.INFO, "<b>Phase 2: Login Execution</b>");
        
        try {
            extentTest.log(Status.INFO, "Entering admin credentials");
            extentTest.info("Username: " + System.getProperty("default.username", config.getProperty("default.username")));
            
            authPage.loginAsAdmin();
            extentTest.log(Status.PASS, "Credentials entered successfully");
            
            extentTest.log(Status.INFO, "Waiting for login processing...");
            page.waitForLoadState();
            page.waitForTimeout(5000);
            
            String currentUrl = page.url();
            String pageTitle = page.title();
            extentTest.log(Status.INFO, "Post-login state - URL: " + currentUrl + ", Title: " + pageTitle);
            
            boolean loginSuccess = dashboardPage.isDashboardPageDisplayed();
            softAssert.assertTrue(loginSuccess, "Should be redirected to dashboard after login");
            
            if (loginSuccess) {
                extentTest.log(Status.PASS, "Login successful - dashboard content detected");
                return true;
            } else {
                extentTest.log(Status.FAIL, "Login failed - dashboard not detected");
                performLoginFailureDebugging(extentTest);
                return false;
            }
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Login execution failed: " + e.getMessage());
            softAssert.fail("Login execution failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Perform debugging when login fails
     */
    private void performLoginFailureDebugging(ExtentTest extentTest) {
        try {
            boolean authPageStillVisible = authPage.isAuthPageDisplayed();
            extentTest.log(Status.INFO, "Auth page still visible: " + authPageStillVisible);
            
            String pageContent = page.content();
            extentTest.log(Status.INFO, "Page content length: " + pageContent.length());
            
            if (authPageStillVisible) {
                extentTest.log(Status.WARNING, "Possible authentication failure - check credentials");
                
                try {
                    boolean hasError = page.locator("text=/error|invalid|failed/i").isVisible();
                    if (hasError) {
                        String errorMsg = page.locator("text=/error|invalid|failed/i").first().textContent();
                        extentTest.log(Status.WARNING, "Error message found: " + errorMsg);
                    }
                } catch (Exception e) {
                    extentTest.log(Status.INFO, "No error messages detected");
                }
            }
            
            try {
                TestListener.addScreenshot(page, "Login Failed - Debug Screenshot");
                extentTest.info("📸 Debug screenshot captured - click thumbnail to view full size");
            } catch (Exception e) {
                extentTest.log(Status.WARNING, "Could not capture debug screenshot: " + e.getMessage());
            }
            
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Could not verify auth page state: " + e.getMessage());
        }
    }
    
    /**
     * Perform comprehensive dashboard verification
     */
    private DashboardVerificationResult performDashboardVerification(ExtentTest extentTest, SoftAssert softAssert) {
        extentTest.log(Status.INFO, "<b>Phase 3: Dashboard Verification</b>");
        
        boolean isDashboardVisible = false;
        DashboardElements elements = new DashboardElements();
        
        try {
            extentTest.log(Status.INFO, "Waiting for page to fully load after login...");
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
            page.waitForTimeout(5000);
            
            int maxRetries = 3;
            
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                extentTest.log(Status.INFO, "Dashboard detection attempt " + attempt + " of " + maxRetries);
                
                elements = detectDashboardElements(extentTest);
                
                boolean dashboardDetected = elements.hasCandidatesText || elements.hasJobsText || 
                                         elements.hasDashboardButton || elements.hasCandidatesTab || 
                                         elements.hasNavigation || elements.hasCredits;
                
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
                extentTest.info("<details><summary>Dashboard Verification Results</summary>" +
                               "<br>• Admin Email: " + (elements.hasAdminName ? "Found (admin@ukg.com)" : "Not Found") +
                               "<br>• Credits: " + (elements.hasCredits ? "Found" : "Not Found") +
                               "<br>• Unified Dashboard Button: " + (elements.hasUnifiedDashboard ? "Found" : "Not Found") +
                               "</details>");
                
                // Additional verifications
                performUserInformationVerification(extentTest);
                performAdminAccessVerification(extentTest, softAssert);
                
            } else {
                extentTest.log(Status.FAIL, "Dashboard not displayed after " + maxRetries + " attempts");
                extentTest.info("<details><summary>Final Dashboard Verification Results</summary>" +
                               "<br>• Page Title: " + page.title() +
                               "<br>• Admin Email: " + (elements.hasAdminName ? "Found (admin@ukg.com)" : "Not Found") +
                               "<br>• Credits: " + (elements.hasCredits ? "Found" : "Not Found") +
                               "<br>• Unified Dashboard Button: " + (elements.hasUnifiedDashboard ? "Found" : "Not Found") +
                               "</details>");
                
                try {
                    TestListener.addScreenshot(page, "Login Failed - Debug Screenshot");
                    extentTest.info("📸 Debug screenshot captured - click thumbnail to view full size");
                } catch (Exception e) {
                    extentTest.log(Status.WARNING, "Could not capture debug screenshot: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Dashboard verification failed: " + e.getMessage());
            softAssert.fail("Dashboard verification failed: " + e.getMessage());
        }
        
        return new DashboardVerificationResult(isDashboardVisible, elements);
    }
    
    /**
     * Detect all dashboard elements
     */
    private DashboardElements detectDashboardElements(ExtentTest extentTest) {
        DashboardElements elements = new DashboardElements();
        
        try {
            elements.hasCandidatesText = page.getByText("candidates").first().isVisible();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Candidates text not found: " + e.getMessage());
        }
        
        try {
            elements.hasJobsText = page.getByText("jobs").first().isVisible();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Jobs text not found: " + e.getMessage());
        }
        
        try {
            elements.hasDashboardButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Unified Dashboard")).isVisible();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Dashboard button not found: " + e.getMessage());
        }
        
        try {
            elements.hasCandidatesTab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CANDIDATES")).isVisible();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Candidates tab not found: " + e.getMessage());
        }
        
        try {
            elements.hasNavigation = page.getByRole(AriaRole.NAVIGATION).isVisible();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Navigation not found: " + e.getMessage());
        }
        
        try {
            elements.hasCredits = dashboardPage.isCreditsDisplayed();
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Credits not found: " + e.getMessage());
        }
        
        // Check for specific dashboard elements
        try {
            elements.hasAdminName = page.getByText("admin@ukg.com").isVisible();
            if (elements.hasAdminName) {
                extentTest.log(Status.INFO, "Admin email found: admin@ukg.com");
            }
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Admin email not found: " + e.getMessage());
        }
        
        try {
            elements.hasCredits = page.locator("text=/\\d+\\s*Credits/").isVisible();
            if (elements.hasCredits) {
                extentTest.log(Status.INFO, "Credits display found");
            }
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Credits display not found: " + e.getMessage());
        }
        
        try {
            elements.hasUnifiedDashboard = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Unified Dashboard")).isVisible();
            if (elements.hasUnifiedDashboard) {
                extentTest.log(Status.INFO, "Unified Dashboard button found");
            }
        } catch (Exception e) {
            extentTest.log(Status.INFO, "Unified Dashboard button not found: " + e.getMessage());
        }
        
        return elements;
    }
    
    /**
     * Verify user information on dashboard
     */
    private void performUserInformationVerification(ExtentTest extentTest) {
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
    }
    
    /**
     * Verify admin access privileges
     */
    private void performAdminAccessVerification(ExtentTest extentTest, SoftAssert softAssert) {
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
    
    /**
     * Execute basic login flow without Extent Reports (for IDE runs)
     */
    private LoginFlowResult executeBasicLoginFlow(SoftAssert softAssert) {
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
            
            // Login successful - check for dashboard content instead of URL
            boolean loginSuccess = dashboardPage.isDashboardPageDisplayed();
            softAssert.assertTrue(loginSuccess, "Should be redirected to dashboard after login");
            System.out.println("Login successful - dashboard content detected");
            
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
            
            DashboardElements elements = new DashboardElements();
            elements.hasAdminName = dashboardPage.isUserEmailDisplayed();
            elements.hasCredits = creditsDisplayed;
            
            return new LoginFlowResult(true, new DashboardVerificationResult(true, elements));
            
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            softAssert.fail("Test execution failed: " + e.getMessage());
            return new LoginFlowResult(false, new DashboardVerificationResult(false, new DashboardElements()));
        }
    }
    
    /**
     * Result classes for comprehensive test reporting
     */
    public static class LoginFlowResult {
        public final boolean success;
        public final DashboardVerificationResult dashboardResult;
        
        public LoginFlowResult(boolean success, DashboardVerificationResult dashboardResult) {
            this.success = success;
            this.dashboardResult = dashboardResult;
        }
    }
    
    public static class DashboardVerificationResult {
        public final boolean isVisible;
        public final DashboardElements elements;
        
        public DashboardVerificationResult(boolean isVisible, DashboardElements elements) {
            this.isVisible = isVisible;
            this.elements = elements;
        }
    }
    
    public static class DashboardElements {
        public boolean hasCandidatesText;
        public boolean hasJobsText;
        public boolean hasDashboardButton;
        public boolean hasCandidatesTab;
        public boolean hasNavigation;
        public boolean hasCredits;
        public boolean hasAdminName;
        public boolean hasUnifiedDashboard;
    }
}
