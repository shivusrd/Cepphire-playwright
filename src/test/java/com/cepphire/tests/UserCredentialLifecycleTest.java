package com.cepphire.tests;

import com.cepphire.pages.UserLifecyclePage;
import com.cepphire.base.BaseTest;
import com.cepphire.utils.JsonDataReader;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import com.cepphire.listeners.TestListener;

@Listeners({TestListener.class})
public class UserCredentialLifecycleTest extends BaseTest {
    
    private UserLifecyclePage userLifecyclePage;
    private JsonDataReader testData;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        if (page != null && config != null) {
            userLifecyclePage = new UserLifecyclePage(page, config);
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
        
        // Execute the complete user credential lifecycle
        UserLifecyclePage.LifecycleResult result = userLifecyclePage.executeUserCredentialLifecycle(
            adminEmail, adminPassword, testUserEmail, testUserPassword, testUserRole, testUserDisplayName);
        
        // Verify the overall test result
        assert result.success : "User credential lifecycle should complete successfully";
        
        // Additional verifications for each phase
        assert result.issueSuccess : "Phase 1 - Issue credentials should succeed";
        assert result.initSuccess : "Phase 2 - User initialization should succeed";
        assert result.revokeSuccess : "Phase 3 - Revoke access should succeed";
        
        System.out.println("✅ User credential lifecycle test completed successfully");
        System.out.println("✅ Phase 1 - Issue credentials: " + (result.issueSuccess ? "PASSED" : "FAILED"));
        System.out.println("✅ Phase 2 - User initialization: " + (result.initSuccess ? "PASSED" : "FAILED"));
        System.out.println("✅ Phase 3 - Revoke access: " + (result.revokeSuccess ? "PASSED" : "FAILED"));
    }
    
    @AfterMethod
    public void tearDownPages() {
        // Clean up any remaining state
        userLifecyclePage = null;
        testData = null;
        
        // Reset viewport to default
        if (page != null) {
            page.setViewportSize(1280, 720);
        }
    }
}
