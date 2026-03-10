package com.nanolink.service;

import com.nanolink.exception.UrlNotFoundException;
import com.nanolink.models.Url;
import com.nanolink.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Separate service for cacheable URL operations.
 * Spring AOP caching only works on external method calls through proxies.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCacheService {

    private final UrlRepository urlRepository;

    @Cacheable(value = "urls", key = "#shortCode", unless = "#result == null")
    @Transactional(readOnly = true)
    public Url getByShortCode(String shortCode) {
        log.info("Cache MISS - Fetching from database: {}", shortCode);
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
    }

    @CacheEvict(value = "urls", key = "#shortCode")
    public void evictCache(String shortCode) {
        log.info("Evicting cache for: {}", shortCode);
    }
}
