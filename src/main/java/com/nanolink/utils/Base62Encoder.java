package com.nanolink.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    public String encode(Long id) {
        if (id == 0 || id == null) {
            return BASE62_CHARS.substring(0, 1);
        }

        StringBuilder encoded = new StringBuilder();
        Long num = id;

        while (num > 0) {
            encoded.insert(0, BASE62_CHARS.charAt((int) (num % BASE)));
            num /= BASE;
        }

        return encoded.toString();
    }

    public Long decode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            return 0L;
        }

        long decoded = 0;
        for (int i = 0; i < shortCode.length(); i++) {
            char c = shortCode.charAt(i);
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in short code: " + c);
            }
            decoded = decoded * BASE + index;
        }
        return decoded;
    }
}
