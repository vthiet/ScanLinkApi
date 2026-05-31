package com.example.scanlink.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileHistoryResponse {
    private String fileName;
    private String fileUrl;
    private LocalDateTime createdAt;
}
