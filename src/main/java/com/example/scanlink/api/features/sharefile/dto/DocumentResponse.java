package com.example.scanlink.api.features.sharefile.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "ownerId", "title", "storageUrl", "fileSize","extractedText","createdAt","updatedAt"})
public class DocumentResponse {
    private String Id;
    private String ownerId;
    private String title;
    private String storageUrl;
    private Long fileSize;
    private String extractedText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
