package com.nanolink.service;

import com.nanolink.dto.ShortenUrlRequest;
import com.nanolink.dto.ShortenUrlResponse;
import com.nanolink.dto.UpdateUrlRequest;
import com.nanolink.models.Url;
import com.nanolink.exception.InvalidUrlException;
import com.nanolink.exception.ShortCodeAlreadyExistsException;
import com.nanolink.exception.UrlExpiredException;
import com.nanolink.exception.UrlNotFoundException;
import com.nanolink.repository.UrlRepository;
import com.nanolink.utils.ShortCodeGenerator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ClickService clickService;
    private final UrlCacheService urlCacheService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        log.info("Shortening URL: {}", request.getUrl());

        validateUrl(request.getUrl());
        String shortCode = determineShortCode(request.getCustomShortCode());
        LocalDateTime expiresAt = calculateExpiration(request.getExpirationDays());

        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(request.getUrl())
                .expiresAt(expiresAt)
                .build();

        Url savedUrl = urlRepository.save(url);
        log.info("Created short URL: {} -> {}", shortCode, request.getUrl());

        return ShortenUrlResponse.builder()
                .shortCode(savedUrl.getShortCode())
                .shortUrl(baseUrl + "/" + savedUrl.getShortCode())
                .originalUrl(savedUrl.getOriginalUrl())
                .createdAt(savedUrl.getCreatedAt())
                .expiresAt(savedUrl.getExpiresAt())
                .build();
    }

    @Transactional
    public String getOriginalUrlAndTrack(String shortCode, HttpServletRequest request) {
        log.info("Looking up short code: {}", shortCode);

        Url url = urlCacheService.getByShortCode(shortCode);

        if (!url.getIsActive()) {
            log.warn("Attempted to access inactive URL: {}", shortCode);
            throw new UrlNotFoundException("This short URL has been deactivated");
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Attempted to access expired URL: {}", shortCode);
            throw new UrlExpiredException("This short URL has expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        clickService.trackClick(url, request);

        log.info("Redirecting {} to {} (clicks: {})", shortCode, url.getOriginalUrl(), url.getClickCount());

        return url.getOriginalUrl();
    }

    private void validateUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            
            String protocol = url.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new InvalidUrlException("Only HTTP and HTTPS protocols are supported");
            }
            
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Invalid URL format: {}", urlString, e);
            throw new InvalidUrlException("Invalid URL format: " + urlString);
        }
    }

    private String determineShortCode(String customShortCode) {
        if (customShortCode != null && !customShortCode.isEmpty()) {
            if (!shortCodeGenerator.isValidCustomShortCode(customShortCode)) {
                throw new InvalidUrlException("Invalid custom short code format");
            }
            
            if (urlRepository.existsByShortCode(customShortCode)) {
                throw new ShortCodeAlreadyExistsException(
                    "Short code '" + customShortCode + "' is already in use"
                );
            }
            
            log.info("Using custom short code: {}", customShortCode);
            return customShortCode;
        }
        
        return shortCodeGenerator.generateUniqueShortCode();
    }

    private LocalDateTime calculateExpiration(Integer expirationDays) {
        if (expirationDays != null && expirationDays > 0) {
            return LocalDateTime.now().plusDays(expirationDays);
        }
        return null;
    }

    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public ShortenUrlResponse deactivateUrl(String shortCode) {
        log.info("Deactivating URL: {}", shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        url.setIsActive(false);
        Url updated = urlRepository.save(url);

        return buildResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public ShortenUrlResponse reactivateUrl(String shortCode) {
        log.info("Reactivating URL: {}", shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        url.setIsActive(true);
        Url updated = urlRepository.save(url);

        return buildResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public void deleteUrl(String shortCode) {
        log.info("Deleting URL: {}", shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        urlRepository.delete(url);
    }

    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public ShortenUrlResponse updateUrl(String shortCode, UpdateUrlRequest request) {
        log.info("Updating URL: {}", shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        if (request.getIsActive() != null) {
            url.setIsActive(request.getIsActive());
        }

        if (request.getExpirationDays() != null) {
            url.setExpiresAt(LocalDateTime.now().plusDays(request.getExpirationDays()));
        }

        Url updated = urlRepository.save(url);

        return buildResponse(updated);
    }

    private ShortenUrlResponse buildResponse(Url url) {
        return ShortenUrlResponse.builder()
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();
    }
}