package com.nanolink.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanolink.dto.ErrorResponse;
import com.nanolink.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Access denied. You do not have the required role or permission.")
                .code(ErrorCode.AUTH_INSUFFICIENT_PERMISSIONS)
                .build();
                
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}