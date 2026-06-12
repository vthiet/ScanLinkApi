package com.example.scanlink.api.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String uid;
    private String email;
    private String displayName;
    private String dateOfBirth;
    private String gender;
    private String role;
    private boolean active;
    private long createdAt;
    private long updatedAt;
}
