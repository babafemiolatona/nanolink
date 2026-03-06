package com.nanolink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlStatsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    
    // Recent clicks (last 10)
    private List<ClickInfo> recentClicks;
    
    // Clicks by date (for charts)
    private Map<String, Long> clicksByDate;
    
    // Device breakdown
    private Map<String, Long> deviceDistribution;
    
    // Browser breakdown
    private Map<String, Long> browserDistribution;
    
    // OS breakdown
    private Map<String, Long> osDistribution;
    
    // Top countries (if available)
    private Map<String, Long> topCountries;
}