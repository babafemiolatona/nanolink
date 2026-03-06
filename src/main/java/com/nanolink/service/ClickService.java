package com.nanolink.service;

import com.nanolink.models.Click;
import com.nanolink.models.Url;
import com.nanolink.repository.ClickRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickService {

    private final ClickRepository clickRepository;

    @Transactional
    public void trackClick(Url url, HttpServletRequest request) {
        try {
            String ipAddress = extractIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referrer = request.getHeader("Referer");
            
            DeviceInfo deviceInfo = parseUserAgent(userAgent);
            
            Click click = Click.builder()
                    .url(url)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .deviceType(deviceInfo.deviceType)
                    .browser(deviceInfo.browser)
                    .operatingSystem(deviceInfo.operatingSystem)
                    .build();
            
            clickRepository.save(click);
            
            log.info("Tracked click for URL {} from IP {} using {}", 
                    url.getShortCode(), ipAddress, deviceInfo.browser);
                    
        } catch (Exception e) {
            log.error("Failed to track click for URL {}: {}", url.getShortCode(), e.getMessage());
        }
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private DeviceInfo parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return new DeviceInfo("Unknown", "Unknown", "Unknown");
        }
        
        String deviceType = detectDeviceType(userAgent);
        String browser = detectBrowser(userAgent);
        String os = detectOperatingSystem(userAgent);
        
        return new DeviceInfo(deviceType, browser, os);
    }

    private String detectDeviceType(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "Mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "Tablet";
        }
        return "Desktop";
    }

    private String detectBrowser(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/")) return "Edge";
        if (ua.contains("chrome/") && !ua.contains("edg/")) return "Chrome";
        if (ua.contains("firefox/")) return "Firefox";
        if (ua.contains("safari/") && !ua.contains("chrome/")) return "Safari";
        if (ua.contains("opera/") || ua.contains("opr/")) return "Opera";
        return "Other";
    }

    private String detectOperatingSystem(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac os x") || ua.contains("macos")) return "macOS";
        if (ua.contains("linux")) return "Linux";
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        return "Other";
    }

    private static class DeviceInfo {
        String deviceType;
        String browser;
        String operatingSystem;
        
        DeviceInfo(String deviceType, String browser, String operatingSystem) {
            this.deviceType = deviceType;
            this.browser = browser;
            this.operatingSystem = operatingSystem;
        }
    }
}