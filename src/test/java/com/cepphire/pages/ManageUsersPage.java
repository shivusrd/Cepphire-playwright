package com.cepphire.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import java.util.Properties;

public class ManageUsersPage {
    
    private final Page page;
    private final Properties config;
    
    public ManageUsersPage(Page page, Properties config) {
        this.page = page;
        this.config = config;
    }
    
    /**
     * Navigate to Manage Users page
     */
    public void navigateToManageUsers() {
        page.navigate(config.getProperty("base.url") + "/admin/manage-users");
        waitForPageLoad();
    }
    
    /**
     * Enter user email for credential management
     */
    public void enterUserEmail(String email) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("user@gmail.com")).fill(email);
    }
    
    /**
     * Select role for user (recruiter, candidate, etc.)
     */
    public void selectRole(String role) {
        page.getByRole(AriaRole.COMPLEMENTARY).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(role)).click();
    }
    
    /**
     * Issue credentials to a user
     */
    public void issueCredentials() {
        // Handle dialog that appears when issuing credentials
        page.onceDialog(dialog -> {
            System.out.println("Issue Credentials Dialog: " + dialog.message());
            dialog.dismiss();
        });
        
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Issue Credentials")).click();
        page.waitForLoadState();
    }
    
    /**
     * Revoke access from a user
     */
    public void revokeAccess() {
        // Handle dialog that appears when revoking access
        page.onceDialog(dialog -> {
            System.out.println("Revoke Access Dialog: " + dialog.message());
            dialog.dismiss();
        });
        
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Revoke Access")).first().click();
        page.waitForLoadState();
    }
    
    /**
     * Click on user profile/link
     */
    public void clickUserProfile(String userName) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(userName)).click();
        page.waitForLoadState();
    }
    
    /**
     * Wait for page to load
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        page.waitForTimeout(2000);
    }
    
    /**
     * Check if manage users page is displayed
     */
    public boolean isManageUsersPageDisplayed() {
        return page.url().contains("/admin/manage-users");
    }
    
    /**
     * Check if user exists in the user list
     */
    public boolean isUserPresent(String userEmail) {
        return page.getByText(userEmail).isVisible();
    }
    
    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }
}
