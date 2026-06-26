package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SharePrivateResponse {
    String documentId;
    String collaboratorEmail;
    PermissionRole permissionRole;
}
