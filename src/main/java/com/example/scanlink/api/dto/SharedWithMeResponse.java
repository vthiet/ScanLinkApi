package com.example.scanlink.api.dto;

import com.example.scanlink.api.model.enums.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
public class SharedWithMeResponse {
    private String fileId;
    private String fileName;
    private String fileUrl;
    private String shareName;
    private PermissionRole role;
    private LocalDateTime shareAt;
}
