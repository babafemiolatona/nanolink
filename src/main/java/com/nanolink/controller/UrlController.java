package com.nanolink.controller;

import com.nanolink.dto.ShortenUrlRequest;
import com.nanolink.dto.ShortenUrlResponse;
import com.nanolink.dto.UpdateUrlRequest;
import com.nanolink.dto.UrlStatsResponse;
import com.nanolink.service.UrlService;
import com.nanolink.service.AnalyticsService;
import com.nanolink.service.RateLimitService;

import com.nanolink.utils.InputSanitizer;

import com.nanolink.exception.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;
    private final RateLimitService rateLimitService;
    private final InputSanitizer inputSanitizer;

    @Operation(
        summary = "Shorten a URL",
        description = "Creates a shortened version of the provided URL. Optionally allows custom short codes and expiration."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "URL successfully shortened",
            content = @Content(schema = @Schema(implementation = ShortenUrlResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid URL or request parameters"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Custom short code already exists"
        )
    })
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request,
            HttpServletRequest httpRequest) {
        
        if (!rateLimitService.isAllowed(httpRequest)) {
            throw new RateLimitExceededException("Too many requests. Please try again in a limit.");
        }

        String sanitizedUrl = inputSanitizer.sanitizeUrl(request.getUrl());
        request.setUrl(sanitizedUrl);

        if (request.getCustomShortCode() != null) {
            String sanitizedCode = inputSanitizer.sanitizeShortCode(request.getCustomShortCode());
            request.setCustomShortCode(sanitizedCode);
        }

        log.info("Received shorten URL request for: {}", request.getUrl());
        ShortenUrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Redirect to original URL",
        description = "Redirects the user to the original URL associated with the short code. " +
                     "Increments the click counter for analytics."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "302",
            description = "Redirect to original URL"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Short code not found or URL is inactive"
        ),
        @ApiResponse(
            responseCode = "410",
            description = "URL has expired"
        )
    })
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(
            @Parameter(description = "The short code to redirect", example = "aB3xY9k")
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        log.info("Redirect request for short code: {}", shortCode);
        
        String originalUrl = urlService.getOriginalUrlAndTrack(shortCode, request);
        
        RedirectView redirectView = new RedirectView(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        
        return redirectView;
    }

    @Operation(
        summary = "Get URL statistics",
        description = "Retrieves detailed analytics for a short URL including click count, device breakdown, browser stats, and geographic data."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = UrlStatsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Short code not found"
        )
    })
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<UrlStatsResponse> getUrlStats(
            @Parameter(description = "The short code to get statistics for", example = "aB3xY9k")
            @PathVariable String shortCode) {
        
        log.info("Fetching statistics for short code: {}", shortCode);
        UrlStatsResponse stats = analyticsService.getUrlStats(shortCode);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Deactivate a URL", description = "Soft delete - prevents redirects but keeps analytics")
    @ApiResponse(responseCode = "200", description = "URL deactivated successfully")
    @PatchMapping("/urls/{shortCode}/deactivate")
    public ResponseEntity<ShortenUrlResponse> deactivateUrl(
            @Parameter(description = "Short code to deactivate")
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        if (!rateLimitService.isAllowed(request)) {
            throw new RateLimitExceededException("Too many requests from your IP");
        }
        
        ShortenUrlResponse response = urlService.deactivateUrl(shortCode);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reactivate a URL", description = "Re-enable a deactivated URL")
    @ApiResponse(responseCode = "200", description = "URL reactivated successfully")
    @PatchMapping("/urls/{shortCode}/reactivate")
    public ResponseEntity<ShortenUrlResponse> reactivateUrl(
            @Parameter(description = "Short code to reactivate")
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        if (!rateLimitService.isAllowed(request)) {
            throw new RateLimitExceededException("Too many requests from your IP");
        }
        
        ShortenUrlResponse response = urlService.reactivateUrl(shortCode);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update URL settings", description = "Update active status or expiration")
    @ApiResponse(responseCode = "200", description = "URL updated successfully")
    @PatchMapping("/urls/{shortCode}")
    public ResponseEntity<ShortenUrlResponse> updateUrl(
            @Parameter(description = "Short code to update")
            @PathVariable String shortCode,
            @Valid @RequestBody UpdateUrlRequest request,
            HttpServletRequest httpRequest) {
        
        if (!rateLimitService.isAllowed(httpRequest)) {
            throw new RateLimitExceededException("Too many requests from your IP");
        }
        
        ShortenUrlResponse response = urlService.updateUrl(shortCode, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Permanently delete a URL", description = "Hard delete - removes URL and all analytics")
    @ApiResponse(responseCode = "204", description = "URL deleted successfully")
    @DeleteMapping("/urls/{shortCode}/permanent")
    public ResponseEntity<Void> deleteUrlPermanently(
            @Parameter(description = "Short code to delete permanently")
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        if (!rateLimitService.isAllowed(request)) {
            throw new RateLimitExceededException("Too many requests from your IP");
        }
        
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("NanoLink API is running!");
    }
}