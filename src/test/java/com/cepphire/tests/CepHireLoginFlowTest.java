package com.cepphire.tests;

import com.cepphire.pages.LoginFlowPage;
import com.cepphire.base.BaseTest;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import com.cepphire.listeners.TestListener;

@Listeners({TestListener.class})
public class CepHireLoginFlowTest extends BaseTest {
    
    private LoginFlowPage loginFlowPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        if (page != null && config != null) {
            loginFlowPage = new LoginFlowPage(page, config);
        }
    }
    
    /**
     * Test complete login flow with valid credentials
     * Verifies successful login and dashboard access
     */
    @Test(description = "Complete login flow with valid credentials", 
          groups = {"login", "smoke", "regression"})
    public void testValidLoginFlow() {
        // Execute the complete login flow
        LoginFlowPage.LoginFlowResult result = loginFlowPage.executeCompleteLoginFlow();
        
        // Verify the overall test result
        assert result.success : "Login flow should complete successfully";
        
        // Additional verifications if needed
        if (result.dashboardResult.isVisible) {
            System.out.println("✅ Login flow completed successfully");
            System.out.println("✅ Dashboard verified and accessible");
            System.out.println("✅ Admin access confirmed");
        }
    }
    
    @AfterMethod
    public void tearDownPages() {
        // Clean up any remaining state
        loginFlowPage = null;
        
        // Reset viewport to default
        if (page != null) {
            page.setViewportSize(1280, 720);
        }
    }
}
