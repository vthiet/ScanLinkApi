package com.example.scanlink.api.features.sharefile.dto;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentHistoryResponse (
         String id,
         String title,
         String storageUrl,   // generate từ publicId
         long fileSize,
         LocalDateTime createdAt){}
