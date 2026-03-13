package com.nanolink.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanolink.dto.ErrorResponse;
import com.nanolink.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Authentication required. Please provide a valid JWT token.")
                .code(ErrorCode.AUTH_REQUIRED)
                .build();
                
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}