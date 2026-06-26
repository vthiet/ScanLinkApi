package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SharePublicRequest {
    String documentId;
    String password;
    int expireInDays;
    PermissionRole permissionRole;
}
