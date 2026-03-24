package com.nanolink.service;

import com.nanolink.dto.CachedUrl;
import com.nanolink.exception.UrlNotFoundException;
import com.nanolink.models.Url;
import com.nanolink.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCacheService {

    private final UrlRepository urlRepository;

    @Cacheable(value = "urls", key = "#shortCode", unless = "#result == null")
    @Transactional(readOnly = true)
        public CachedUrl getByShortCode(String shortCode) {
        log.info("Cache MISS - Fetching from database: {}", shortCode);
        Url url = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        return CachedUrl.builder()
            .id(url.getId())
            .shortCode(url.getShortCode())
            .originalUrl(url.getOriginalUrl())
            .expiresAt(url.getExpiresAt())
            .isActive(url.getIsActive())
            .build();
    }

    @CacheEvict(value = "urls", key = "#shortCode")
    public void evictCache(String shortCode) {
        log.info("Evicting cache for: {}", shortCode);
    }
}
