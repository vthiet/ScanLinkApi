package com.example.scanlink.api.features.sharefile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class FileDownloadResponse {
    private String fileName;
    private MultipartFile file;
    private String title;
    private String extractText;
}
