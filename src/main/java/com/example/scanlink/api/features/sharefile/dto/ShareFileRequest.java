package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import lombok.Data;


@Data
public class ShareFileRequest {
    private String fileId;
    private String ownerUserId;
    private String targetUserId;
    private PermissionRole role;
    private Visibility visibility;
}
