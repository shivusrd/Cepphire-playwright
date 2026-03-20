package com.cepphire.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class JsonDataReader {
    
    private JsonNode rootNode;
    private ObjectMapper objectMapper;
    
    public JsonDataReader(String jsonFilePath) throws IOException {
        objectMapper = new ObjectMapper();
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            throw new IOException("JSON test data file not found: " + jsonFilePath);
        }
        rootNode = objectMapper.readTree(file);
    }
    
    public String getUsername(String userType) {
        return rootNode.path("login")
                .path("valid")
                .path(userType)
                .path("username")
                .asText();
    }
    
    public String getPassword(String userType) {
        return rootNode.path("login")
                .path("valid")
                .path(userType)
                .path("password")
                .asText();
    }
    
    public String getExpectedUrl(String userType) {
        return rootNode.path("login")
                .path("valid")
                .path(userType)
                .path("expectedUrl")
                .asText();
    }
    
    public String getOrganizationName(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("name")
                .asText();
    }
    
    public String getOrganizationIndustry(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("industry")
                .asText();
    }
    
    public String getOrganizationSize(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("size")
                .asText();
    }
    
    public String getOrganizationDescription(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("description")
                .asText();
    }
    
    public String getOrganizationWebsite(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("website")
                .asText();
    }
    
    public String getExpectedOrgStatus(String orgType) {
        return rootNode.path("organization")
                .path("valid")
                .path(orgType)
                .path("expectedStatus")
                .asText();
    }
    
    public String getBaseUrl() {
        return rootNode.path("urls")
                .path("base")
                .asText();
    }
    
    public String getLoginUrl() {
        return rootNode.path("urls")
                .path("login")
                .asText();
    }
    
    public String getDashboardUrl() {
        return rootNode.path("urls")
                .path("dashboard")
                .asText();
    }
    
    public String getSetupOrgUrl() {
        return rootNode.path("urls")
                .path("setupOrg")
                .asText();
    }
    
    public int getTimeout(String timeoutType) {
        return rootNode.path("timeouts")
                .path(timeoutType)
                .asInt();
    }
    
    public String getSelector(String page, String element) {
        return rootNode.path("selectors")
                .path(page)
                .path(element)
                .asText();
    }
    
    public String getInvalidLoginUsername(String invalidType) {
        return rootNode.path("login")
                .path("invalid")
                .path(invalidType)
                .path("username")
                .asText();
    }
    
    public String getInvalidLoginPassword(String invalidType) {
        return rootNode.path("login")
                .path("invalid")
                .path(invalidType)
                .path("password")
                .asText();
    }
    
    public String getInvalidLoginErrorMessage(String invalidType) {
        return rootNode.path("login")
                .path("invalid")
                .path(invalidType)
                .path("errorMessage")
                .asText();
    }
    
    public JsonNode getRootNode() {
        return rootNode;
    }
    
    public JsonNode getNode(String... path) {
        JsonNode current = rootNode;
        for (String segment : path) {
            current = current.path(segment);
        }
        return current;
    }
    
    public JsonNode getUserData(String userType) {
        return rootNode.path("users").path(userType);
    }
    
    public String getUserEmail(String userType) {
        return rootNode.path("users")
                .path(userType)
                .path("email")
                .asText();
    }
    
    public String getUserPassword(String userType) {
        return rootNode.path("users")
                .path(userType)
                .path("password")
                .asText();
    }
    
    public String getUserRole(String userType) {
        return rootNode.path("users")
                .path(userType)
                .path("role")
                .asText();
    }
    
    public String getUserDisplayName(String userType) {
        return rootNode.path("users")
                .path(userType)
                .path("displayName")
                .asText();
    }
}
