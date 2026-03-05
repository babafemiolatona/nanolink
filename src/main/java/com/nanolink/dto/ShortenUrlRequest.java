package com.nanolink.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    @Pattern(
        regexp = "^https?://.*",
        message = "URL must start with http:// or https://"
    )
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    private String url;

    @Size(min = 3, max = 20, message = "Custom short code must be between 3 and 20 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*$",
        message = "Custom short code can only contain letters, numbers, hyphens, and underscores"
    )
    private String customShortCode;

    @Min(value = 1, message = "Expiration days must be a positive integer")
    private Integer expirationDays;

}
