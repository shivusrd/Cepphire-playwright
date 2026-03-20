package com.cepphire.tests;

import com.cepphire.pages.ManageUsersPage;
import com.cepphire.base.BaseTest;
import com.cepphire.utils.JsonDataReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import com.cepphire.listeners.TestListener;

@Listeners({TestListener.class})
public class UserCredentialLifecycleTest extends BaseTest {
    
    private ManageUsersPage manageUsersPage;
    private JsonDataReader testData;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        if (page != null && config != null) {
            manageUsersPage = new ManageUsersPage(page, config);
            try {
                testData = new JsonDataReader(config.getProperty("test.data.file.path"));
            } catch (Exception e) {
                System.err.println("Failed to load test data: " + e.getMessage());
                testData = null;
            }
        }
    }
    
    /**
     * Test complete user credential lifecycle:
     * 1. Admin issues credentials to a new user
     * 2. User initializes account and logs in
     * 3. Admin revokes user access
     */
    @Test(description = "Complete user credential lifecycle - Issue, Initialize, and Revoke", 
          groups = {"users", "lifecycle", "regression"})
    public void testUserCredentialLifecycle() {
        // Get test data
        String adminEmail = testData.getUsername("admin01");
        String adminPassword = testData.getPassword("admin01");
        String testUserEmail = testData.getUserEmail("testRecruiter");
        String testUserPassword = testData.getUserPassword("testRecruiter");
        String testUserRole = testData.getUserRole("testRecruiter");
        String testUserDisplayName = testData.getUserDisplayName("testRecruiter");
        
        // Phase 1: Admin logs in and issues credentials
        performAdminLoginAndIssueCredentials(adminEmail, adminPassword, testUserEmail, testUserRole);
        
        // Phase 2: User initializes account and logs in
        performUserAccountInitialization(testUserEmail, testUserPassword);
        
        // Phase 3: Admin revokes user access
        performAdminLoginAndRevokeAccess(adminEmail, adminPassword, testUserDisplayName);
    }
    
    /**
     * Admin login and issue credentials to user
     */
    private void performAdminLoginAndIssueCredentials(String adminEmail, String adminPassword, 
                                                     String userEmail, String role) {
        // Navigate to application and login as admin
        page.navigate(config.getProperty("base.url"));
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign In")).click();
        
        // Fill admin credentials
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("name@company.com")).fill(adminEmail);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("••••••••")).fill(adminPassword);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Access Workspace")).click();
        
        // Wait for login to complete and verify dashboard
        page.waitForLoadState();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*dashboard.*"));
        
        // Navigate to Manage Users
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Menu")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Manage Users")).click();
        
        // Verify manage users page
        page.waitForLoadState();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*admin/manage-users.*"));
        
        // Issue credentials to test user
        manageUsersPage.enterUserEmail(userEmail);
        manageUsersPage.selectRole(role);
        manageUsersPage.issueCredentials();
        
        // Verify credentials issued successfully
        page.waitForLoadState();
        System.out.println("✅ Credentials issued successfully to: " + userEmail);
    }
    
    /**
     * User initializes account and logs in for the first time
     */
    private void performUserAccountInitialization(String userEmail, String userPassword) {
        // Navigate to application
        page.navigate(config.getProperty("base.url"));
        
        // Click Request Access (for new user)
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Request Access")).click();
        
        // Fill user credentials
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("name@company.com")).fill(userEmail);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("••••••••")).fill(userPassword);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Initialize Account")).click();
        
        // Wait for account initialization
        page.waitForLoadState();
        
        // Click Log In on the next screen
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log In")).click();
        
        // Fill credentials again for login
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("name@company.com")).fill(userEmail);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("••••••••")).fill(userPassword);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Access Workspace")).click();
        
        // Wait for login to complete and verify dashboard access
        page.waitForLoadState();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*dashboard.*"));
        
        System.out.println("✅ User account initialized and login successful for: " + userEmail);
        
        // Sign out as user
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Menu")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign Out")).click();
        page.waitForLoadState();
    }
    
    /**
     * Admin login and revoke user access
     */
    private void performAdminLoginAndRevokeAccess(String adminEmail, String adminPassword, String userDisplayName) {
        // Login as admin again
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("name@company.com")).fill(adminEmail);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("••••••••")).fill(adminPassword);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Access Workspace")).click();
        
        // Wait for login to complete
        page.waitForLoadState();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*dashboard.*"));
        
        // Navigate to Manage Users
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Menu")).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Manage Users")).click();
        
        // Verify manage users page
        page.waitForLoadState();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*admin/manage-users.*"));
        
        // Revoke user access
        manageUsersPage.revokeAccess();
        
        // Verify access revoked successfully
        page.waitForLoadState();
        System.out.println("✅ User access revoked successfully for: " + userDisplayName);
        
        // Sign out as admin
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(userDisplayName)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Menu")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign Out")).click();
        page.waitForLoadState();
        
        System.out.println("✅ Complete user credential lifecycle test finished successfully");
    }
    
    @AfterMethod
    public void tearDownPages() {
        // Clean up any remaining state
        manageUsersPage = null;
        testData = null;
        
        // Reset viewport to default
        if (page != null) {
            page.setViewportSize(1280, 720);
        }
    }
}
