package com.example.scanlink.api.dto;

import com.example.scanlink.api.model.enums.PermissionRole;
import com.example.scanlink.api.model.enums.Visibility;
import lombok.Data;

@Data
public class UpdateVisibilityRequest {
    private String fileShareId;
    private String ownerUserId;
    private Visibility visibility;
}
