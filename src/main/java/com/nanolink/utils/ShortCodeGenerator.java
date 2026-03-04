package com.nanolink.utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.nanolink.repository.UrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShortCodeGenerator {

    private final UrlRepository urlRepository;
    private final Base62Encoder base62Encoder;
    
    private static final int SHORT_CODE_LENGTH = 7;
    private static final int MAX_RETRIES = 5;
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String shortCode = generateRandomShortCode();
            if (!urlRepository.existsByShortCode(shortCode)) {
                log.debug("Generated unique short code: {} (attempt {})", shortCode, attempt + 1);
                return shortCode;
            }

            log.warn("Collision detected for short code: {} (attempt {})", shortCode, attempt + 1);
        }

        throw new RuntimeException("Failed to generate a unique short code after " + MAX_RETRIES + " attempts");
    }

    public String generateFromId(Long id) {
        String encoded = base62Encoder.encode(id);

        if (encoded.length() < SHORT_CODE_LENGTH) {
            return String.format("%0" + SHORT_CODE_LENGTH + "d", 0) + encoded;
        }

        return encoded;
    }

    private String generateRandomShortCode() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(BASE62_CHARS.length());
            shortCode.append(BASE62_CHARS.charAt(randomIndex));
        }
        
        return shortCode.toString();
    }

    public boolean isValidCustomShortCode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            return false;
        }
        
        if (shortCode.length() < 3 || shortCode.length() > 20) {
            return false;
        }
        
        return shortCode.matches("^[a-zA-Z0-9_-]+$");
    }
}
