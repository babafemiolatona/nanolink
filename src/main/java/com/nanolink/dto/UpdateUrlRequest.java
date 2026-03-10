package com.nanolink.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUrlRequest {

    private Boolean isActive;
    private Integer expirationDays;

}