package com.example.scanlink.api.dto.response;

import com.example.scanlink.api.entity.UserEntity;
import lombok.Getter;

@Getter
public class RegisterResponseDto {
    private String role;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;

    public RegisterResponseDto(UserEntity userEntity) {
        this.role = userEntity.getRole();
        this.isActive = userEntity.isActive();
        this.createdAt = userEntity.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC);
        this.updatedAt = userEntity.getUpdatedAt().toEpochSecond(java.time.ZoneOffset.UTC);
    }
}
