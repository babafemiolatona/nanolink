package com.nanolink.service;

import com.nanolink.dto.ClickInfo;
import com.nanolink.dto.UrlStatsResponse;
import com.nanolink.models.Click;
import com.nanolink.models.Url;
import com.nanolink.exception.UrlNotFoundException;
import com.nanolink.repository.ClickRepository;
import com.nanolink.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ClickRepository clickRepository;
        private final UrlRepository urlRepository;

        public UrlStatsResponse getUrlStats(String shortCode, String ownerEmail) {
                log.info("Fetching statistics for short code: {} for user: {}", shortCode, ownerEmail);

                Url url = urlRepository.findByShortCodeAndUser_Email(shortCode, ownerEmail)
                                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        List<Click> allClicks = clickRepository.findByUrlOrderByClickedAtDesc(url);

        return UrlStatsResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .isActive(url.getIsActive())
                .recentClicks(getRecentClicks(allClicks, 10))
                .clicksByDate(getClicksByDate(allClicks))
                .deviceDistribution(getDeviceDistribution(allClicks))
                .browserDistribution(getBrowserDistribution(allClicks))
                .osDistribution(getOsDistribution(allClicks))
                .topCountries(getTopCountries(allClicks))
                .build();
    }

    private List<ClickInfo> getRecentClicks(List<Click> clicks, int limit) {
        return clicks.stream()
                .limit(limit)
                .map(click -> ClickInfo.builder()
                        .id(click.getId())
                        .clickedAt(click.getClickedAt())
                        .ipAddress(maskIpAddress(click.getIpAddress()))
                        .userAgent(click.getUserAgent())
                        .referrer(click.getReferrer())
                        .deviceType(click.getDeviceType())
                        .browser(click.getBrowser())
                        .operatingSystem(click.getOperatingSystem())
                        .country(click.getCountry())
                        .city(click.getCity())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getClicksByDate(List<Click> clicks) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return clicks.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickedAt().format(formatter),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getDeviceDistribution(List<Click> clicks) {
        return clicks.stream()
                .filter(click -> click.getDeviceType() != null)
                .collect(Collectors.groupingBy(
                        Click::getDeviceType,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Long> getBrowserDistribution(List<Click> clicks) {
        return clicks.stream()
                .filter(click -> click.getBrowser() != null)
                .collect(Collectors.groupingBy(
                        Click::getBrowser,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Long> getOsDistribution(List<Click> clicks) {
        return clicks.stream()
                .filter(click -> click.getOperatingSystem() != null)
                .collect(Collectors.groupingBy(
                        Click::getOperatingSystem,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Long> getTopCountries(List<Click> clicks) {
        return clicks.stream()
                .filter(click -> click.getCountry() != null)
                .collect(Collectors.groupingBy(
                        Click::getCountry,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private String maskIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "Unknown";
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".x.x";
        }
        
        if (ip.contains(":")) {
            String[] ipv6Parts = ip.split(":");
            if (ipv6Parts.length > 2) {
                return ipv6Parts[0] + ":" + ipv6Parts[1] + ":x:x";
            }
        }
        
        return "Masked";
    }
}