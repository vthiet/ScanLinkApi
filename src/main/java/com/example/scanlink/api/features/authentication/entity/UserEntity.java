package com.example.scanlink.api.features.authentication.entity;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;

    private String role;
    private boolean isActive;
    private String dateOfBirth;
    private String gender;

    // 2 thuộc tính cho Admin API
    private long storageUsed;
    private long storageLimit;
    private String providerId;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

}