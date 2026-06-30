package com.example.scanlink.api.features.sharefile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadFileRequest (
     String userId,
     String fileName,
     Long size,
     String fileUrl,
     String cloudinaryPublicId,
     String type){}
