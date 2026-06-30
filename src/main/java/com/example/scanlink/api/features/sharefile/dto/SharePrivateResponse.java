package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SharePrivateResponse {
    String documentId;
    String collaboratorEmail;
    PermissionRole permissionRole;
}
