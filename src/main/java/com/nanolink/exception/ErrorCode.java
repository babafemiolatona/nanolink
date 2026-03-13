package com.nanolink.exception;

/**
 * Standard error codes for API responses
 */
public class ErrorCode {
    
    // URL-related errors
    public static final String URL_NOT_FOUND = "URL_NOT_FOUND";
    public static final String URL_EXPIRED = "URL_EXPIRED";
    public static final String URL_INACTIVE = "URL_INACTIVE";
    public static final String INVALID_URL = "INVALID_URL";
    public static final String SHORT_CODE_TAKEN = "SHORT_CODE_TAKEN";
    public static final String INVALID_SHORT_CODE = "INVALID_SHORT_CODE";
    
    // Rate limiting
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
    
    // Validation
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    
    // Authentication & Authorization
    public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
    public static final String AUTH_INVALID_TOKEN = "AUTH_INVALID_TOKEN";
    public static final String AUTH_TOKEN_EXPIRED = "AUTH_TOKEN_EXPIRED";
    public static final String AUTH_INSUFFICIENT_PERMISSIONS = "AUTH_INSUFFICIENT_PERMISSIONS";
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTH_USER_ALREADY_EXISTS = "AUTH_USER_ALREADY_EXISTS";
    
    // General
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    
    private ErrorCode() {
        // Prevent instantiation
    }
}
