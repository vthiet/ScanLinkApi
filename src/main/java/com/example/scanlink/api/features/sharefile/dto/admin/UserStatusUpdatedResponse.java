package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserStatusUpdatedResponse {
    private String uid;
    private boolean isActive;
    private LocalDateTime updatedAt;
}
