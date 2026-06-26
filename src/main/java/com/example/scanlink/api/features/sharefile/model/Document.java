package com.example.scanlink.api.features.sharefile.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @NonNull
    private String originalFilename;

    private String cloudinaryPublicId;
}
