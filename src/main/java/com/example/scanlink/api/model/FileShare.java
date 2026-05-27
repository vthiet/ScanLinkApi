package com.example.scanlink.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "file_shares")
public class FileShare {
    @Id
   private String id;

    @Indexed
   private String fileId;

   @Indexed
   private String shareWithUserId;
   private PermissionRole role;
   private LocalDateTime shareAt;

   public FileShare() {
   }

    public FileShare(String id, String fileId, String shareWithUserId, PermissionRole role, LocalDateTime shareAt) {
        this.id = id;
        this.fileId = fileId;
        this.shareWithUserId = shareWithUserId;
        this.role = role;
        this.shareAt = shareAt;
    }
}
