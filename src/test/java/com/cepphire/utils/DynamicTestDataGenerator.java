package com.cepphire.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating dynamic test data to avoid conflicts
 * during parallel test execution across multiple browsers.
 */
public class DynamicTestDataGenerator {
    
    private static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";
    private static final String BASE_EMAIL_DOMAIN = "test.com";
    
    /**
     * Generates a unique email address for testing
     * Format: baseName + shortTimestamp + random + @domain
     */
    public static String generateUniqueEmail(String baseName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return String.format("%s%s%s@%s", baseName, shortTimestamp, random, BASE_EMAIL_DOMAIN);
    }
    
    /**
     * Generates a unique email with specific domain
     */
    public static String generateUniqueEmail(String baseName, String domain) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return String.format("%s%s%s@%s", baseName, shortTimestamp, random, domain);
    }
    
    /**
     * Generates a unique email using UUID (shorter version)
     */
    public static String generateUniqueEmailWithUUID(String baseName) {
        String uuid = UUID.randomUUID().toString().substring(0, 6);
        return String.format("%s%s@%s", baseName, uuid, BASE_EMAIL_DOMAIN);
    }
    
    /**
     * Generates a unique display name (shorter)
     */
    public static String generateUniqueDisplayName(String baseName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return String.format("%s%s%s", baseName, shortTimestamp, random);
    }
    
    /**
     * Generates a unique username (shorter)
     */
    public static String generateUniqueUsername(String baseName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return String.format("%s%s%s", baseName, shortTimestamp, random);
    }
    
    /**
     * Generates a unique organization name (shorter)
     */
    public static String generateUniqueOrgName(String baseName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return String.format("%s%s%s", baseName, shortTimestamp, random);
    }
    
    /**
     * Generates browser-specific unique data (shorter)
     * Format: baseName + browserShort + shortTimestamp + random
     */
    public static String generateBrowserSpecificEmail(String baseName, String browserName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        String browserShort = browserName.substring(0, 1).toUpperCase(); // C, F, W
        return String.format("%s%s%s%s@%s", baseName, browserShort, shortTimestamp, random, BASE_EMAIL_DOMAIN);
    }
    
    /**
     * Generates browser-specific unique display name (shorter)
     */
    public static String generateBrowserSpecificDisplayName(String baseName, String browserName) {
        String shortTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        String browserShort = browserName.substring(0, 1).toUpperCase(); // C, F, W
        return String.format("%s%s%s%s", baseName, browserShort, shortTimestamp, random);
    }
    
    /**
     * Generates a short unique identifier (6 characters)
     */
    public static String generateShortUniqueId() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
    
    /**
     * Generates a medium unique identifier (12 characters)
     */
    public static String generateMediumUniqueId() {
        return UUID.randomUUID().toString().substring(0, 12);
    }
    
    /**
     * Gets current timestamp for test data uniqueness
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }
    
    /**
     * Creates a complete set of unique test data for a user
     */
    public static class UniqueUserData {
        public final String email;
        public final String displayName;
        public final String username;
        public final String uniqueId;
        
        public UniqueUserData(String email, String displayName, String username, String uniqueId) {
            this.email = email;
            this.displayName = displayName;
            this.username = username;
            this.uniqueId = uniqueId;
        }
        
        public UniqueUserData(String baseName, String browserName) {
            this.uniqueId = generateShortUniqueId();
            this.email = generateBrowserSpecificEmail(baseName, browserName);
            this.displayName = generateBrowserSpecificDisplayName(baseName, browserName);
            this.username = generateUniqueUsername(baseName + browserName.substring(0, 1).toLowerCase());
        }
        
        @Override
        public String toString() {
            return String.format("UserData[email=%s, displayName=%s, username=%s, id=%s]", 
                               email, displayName, username, uniqueId);
        }
    }
    
    /**
     * Creates unique test data for recruiter role
     */
    public static UniqueUserData createUniqueRecruiterData(String browserName) {
        String uniqueId = generateShortUniqueId();
        String email = generateBrowserSpecificEmail("recruiter", browserName);
        String displayName = generateBrowserSpecificDisplayName("Recruiter", browserName);
        String username = generateUniqueUsername("recruiter" + browserName.substring(0, 1).toLowerCase());
        return new UniqueUserData(email, displayName, username, uniqueId);
    }
    
    /**
     * Creates unique test data for candidate role
     */
    public static UniqueUserData createUniqueCandidateData(String browserName) {
        String uniqueId = generateShortUniqueId();
        String email = generateBrowserSpecificEmail("candidate", browserName);
        String displayName = generateBrowserSpecificDisplayName("Candidate", browserName);
        String username = generateUniqueUsername("candidate" + browserName.substring(0, 1).toLowerCase());
        return new UniqueUserData(email, displayName, username, uniqueId);
    }
    
    /**
     * Creates unique test data for admin role (usually fixed, but can be made unique if needed)
     */
    public static UniqueUserData createUniqueAdminData(String browserName) {
        // Admin credentials are usually fixed, but we can make display name unique
        String uniqueId = generateShortUniqueId();
        String email = "admin01@gmail.com"; // Keep admin email fixed
        String displayName = generateBrowserSpecificDisplayName("Admin", browserName);
        String username = "admin01"; // Keep admin username fixed
        
        return new UniqueUserData(email, displayName, username, uniqueId);
    }
    
    // Private constructor for utility class
    private DynamicTestDataGenerator() {}
}
