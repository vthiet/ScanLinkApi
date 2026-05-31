package com.example.scanlink.api.model;


import java.time.LocalDateTime;

public class User {
    private String id;

    // Firebase fields
    private String uid;
    private String email;
    private String phoneNumber;
    private String displayName;
    private String photoUrl;
    private Boolean isEmailVerified = false;
    private String providerId;

    // Custom fields
    private String dateOfBirth;
    private String gender;
    private String role;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
