package com.nanolink.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;

}
