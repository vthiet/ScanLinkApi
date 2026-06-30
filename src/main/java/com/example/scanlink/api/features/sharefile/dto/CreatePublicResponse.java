package com.example.scanlink.api.features.sharefile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePublicResponse {
    String hashToken;
    String documentId;
    LocalDateTime expiresAt;
    boolean hasPassword;
    String shareUrl;
}
