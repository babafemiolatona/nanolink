package com.nanolink.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String message;              
    private String code;
    private Map<String, String> details;

}
