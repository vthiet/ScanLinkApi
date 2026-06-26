package com.example.scanlink.api.features.sharefile.model;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection  = "SHARED_LINKS")
public class SharedLink {
    @Id
   private String hashToken;

    @Indexed
   private String documentId;

   @Indexed
   private String shareWithUserId;
   private PermissionRole role;
   private boolean hasPassword;
   private LocalDateTime expiryDate;
   private LocalDateTime shareAt;
}
