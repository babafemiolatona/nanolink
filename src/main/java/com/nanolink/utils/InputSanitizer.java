package com.nanolink.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("('.*(--|;|union|select|insert|update|delete|drop|create).*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern XSS_PATTERN = Pattern.compile("<[^>]*>", Pattern.CASE_INSENSITIVE);

    /**
     * Sanitize URL to prevent malicious input
     */
    public String sanitizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        String sanitized = SCRIPT_PATTERN.matcher(url).replaceAll("");
        sanitized = XSS_PATTERN.matcher(sanitized).replaceAll("");
        
        sanitized = sanitized.trim();
        
        return sanitized;
    }

    public String sanitizeShortCode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            return shortCode;
        }

        return shortCode.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return XSS_PATTERN.matcher(input).find() || SCRIPT_PATTERN.matcher(input).find();
    }
}