package com.example.scanlink.api.features.sharefile.dto;

import lombok.Data;

@Data
public class UploadFileRequest {
    private String userId;
    private String fileName;
    private Long size;
    private String fileUrl;
    private String cloudinaryPublicId;
    private String type;
}
