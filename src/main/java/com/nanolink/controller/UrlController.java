package com.nanolink.controller;

import com.nanolink.dto.ShortenUrlRequest;
import com.nanolink.dto.ShortenUrlResponse;
import com.nanolink.service.UrlService;

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
            @Valid @RequestBody ShortenUrlRequest request) {
        
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

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("NanoLink API is running!");
    }
}