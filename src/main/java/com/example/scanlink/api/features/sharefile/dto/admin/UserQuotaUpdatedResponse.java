package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class UserQuotaUpdatedResponse {
    private String uid;
    private long storageLimit;
    private LocalDateTime updatedAt;
}
