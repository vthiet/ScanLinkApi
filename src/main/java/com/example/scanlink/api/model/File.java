package com.example.scanlink.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collation = "files")
    public class File {
    @Id
    private String id;

    private String fileName;
    private Long size;
    @Indexed
    private String userId;
    private String guestId;
    private String fileUrl;
    private String cloudinaryPublicId;
    private FileType type;
    private String thumbnailUrl;
    private ProcessingStatus status;
    @Indexed
    private LocalDateTime createdAt;

    public File() {
    }

    public File(String thumbnailUrl, String id, String fileName, Long size, String userId, String guestId, String fileUrl, String cloudinaryPublicId, FileType type, ProcessingStatus status, LocalDateTime createdAt) {
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
        this.fileName = fileName;
        this.size = size;
        this.userId = userId;
        this.guestId = guestId;
        this.fileUrl = fileUrl;
        this.cloudinaryPublicId = cloudinaryPublicId;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }
}
