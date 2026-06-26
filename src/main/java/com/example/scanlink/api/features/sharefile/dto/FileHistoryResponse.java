package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileHistoryResponse {
    private String fileId;
    private String fileName;
    private String fileUrl;
    private LocalDateTime createdAt;
    private PermissionRole permissionRole;
    private Visibility visibility;
}
