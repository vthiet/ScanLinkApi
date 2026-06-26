package com.example.scanlink.api.features.sharefile.model;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@Document(collection  = "SHARED_LINKS")
public class SharedLink {
    @Id
   private String hashToken;

    @Indexed
   private String fileId;

   @Indexed
   private String shareWithUserId;
   private PermissionRole role;
   private boolean hasPassword;
   private LocalDateTime expiryDate;
   private LocalDateTime shareAt;

   public SharedLink() {
   }





}
