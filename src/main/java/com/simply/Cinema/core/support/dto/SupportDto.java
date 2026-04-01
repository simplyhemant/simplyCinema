package com.simply.Cinema.core.support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportDto {
    private Long id;
    private String description;
    private String status;
    private boolean isResolved;
    private Long userId;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
