package com.example.scanlink.api.dto;

import com.example.scanlink.api.model.enums.PermissionRole;
import lombok.Data;

@Data
public class UpdatePermissionRequest {
    private String fileShareId;
    private String ownerUserId;
    private PermissionRole newRole;
}
