package com.example.scanlink.api.features.sharefile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadRequest {
    private MultipartFile file;
    private String title;
    private String extractedText;
}
