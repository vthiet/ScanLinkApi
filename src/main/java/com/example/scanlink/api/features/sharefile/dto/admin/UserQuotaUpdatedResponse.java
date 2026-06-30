package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserQuotaUpdatedResponse {
    private String uid;
    private long storageLimit;
    private LocalDateTime updatedAt;
}
