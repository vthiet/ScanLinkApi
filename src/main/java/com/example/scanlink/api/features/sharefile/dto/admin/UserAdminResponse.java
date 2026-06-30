package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminResponse {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private String role;
    private boolean isActive;
    private long storageUsed;
    private long storageLimit;
    private String providerId;
    private LocalDateTime createdAt;
}
