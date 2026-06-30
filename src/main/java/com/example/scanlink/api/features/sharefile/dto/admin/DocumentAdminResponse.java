package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentAdminResponse {
    private String id;
    private String title;
    private String ownerUid;
    private String ownerEmail;
    private Long fileSize;
    private String storageUrl;
    private LocalDateTime createdAt;
}
