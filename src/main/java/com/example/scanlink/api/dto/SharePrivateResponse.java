package com.example.scanlink.api.dto;

import com.example.scanlink.api.model.enums.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SharePrivateResponse {
    String documentId;
    String collaboratorEmail;
    PermissionRole permissionRole;
}
