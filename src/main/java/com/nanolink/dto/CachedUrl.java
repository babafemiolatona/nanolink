package com.nanolink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CachedUrl {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private Boolean isActive;
}
