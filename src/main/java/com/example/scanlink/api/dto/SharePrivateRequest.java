package com.example.scanlink.api.dto;

import com.example.scanlink.api.model.enums.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SharePrivateRequest {
    String documentId;
    String shareToEmail;
    PermissionRole permissionRole;
}
