package com.example.scanlink.api.features.sharefile.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@org.springframework.data.mongodb.core.mapping.Document(collection  = "DOCUMENTS")
    public class Document {
    @Id
    private String id;

    @Indexed
    private String ownerUid;

    private String storageUrl;
    private Long fileSize;
    private String extractedText;
    private List<SharedLink> sharedLinks;
    private List<DocumentPermission> permissions;
    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // nhóm delete
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    public Document() {
    }

    public Document(Boolean isDeleted, String id, String title, Long fileSize, String ownerUid, String storageUrl, String extractedText, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.isDeleted = isDeleted;
        this.id = id;
        this.title = title;
        this.fileSize = fileSize;
        this.ownerUid = ownerUid;
        this.storageUrl = storageUrl;
        this.extractedText = extractedText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
