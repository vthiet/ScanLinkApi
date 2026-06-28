package com.example.scanlink.api.features.sharefile.model;

import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
   private String passwordHash;
   private LocalDateTime expiresAt;
   private LocalDateTime createdAt;
   private boolean hasPassword;
   private String shareUrl;
}
