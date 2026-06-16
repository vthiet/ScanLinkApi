package com.example.scanlink.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String displayName;
    private String dateOfBirth;
    private String gender;
}
