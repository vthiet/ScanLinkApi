package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShareFileRequest {
    private String fileId;
    private String ownerUserId;
    private String targetUserId;
    private PermissionRole role;
    private Visibility visibility;
}
