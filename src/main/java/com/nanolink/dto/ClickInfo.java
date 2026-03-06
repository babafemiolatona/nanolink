package com.nanolink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickInfo {

    private Long id;
    private LocalDateTime clickedAt;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private String country;
    private String city;
}