package com.nanolink.exception;

import com.nanolink.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleShortCodeAlreadyExists(
            ShortCodeAlreadyExistsException ex) {
        
        log.error("Short code already exists: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.SHORT_CODE_TAKEN)
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(
            InvalidUrlException ex) {
        
        log.error("Invalid URL: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.INVALID_URL)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(
            UrlNotFoundException ex) {
        
        log.error("URL not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.URL_NOT_FOUND)
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handleUrlExpired(
            UrlExpiredException ex) {
        
        log.error("URL expired: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.URL_EXPIRED)
                .build();
        
        return ResponseEntity.status(HttpStatus.GONE).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation errors: {}", errors);
        
        ErrorResponse error = ErrorResponse.builder()
                .message("Validation failed")
                .code(ErrorCode.VALIDATION_ERROR)
                .details(errors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex) {

        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message("Too many requests. Please try again later.")
                .code(ErrorCode.RATE_LIMIT_EXCEEDED)
                .build();
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        log.warn("User already exists: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.AUTH_USER_ALREADY_EXISTS)
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        log.warn("Invalid credentials: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ErrorCode.AUTH_INVALID_CREDENTIALS)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .code(ErrorCode.INTERNAL_ERROR)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}