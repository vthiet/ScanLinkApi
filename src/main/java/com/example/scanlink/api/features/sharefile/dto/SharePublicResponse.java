package com.example.scanlink.api.features.sharefile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharePublicResponse {
    String hasToken;
    String documentId;
    LocalDateTime expiryDate;
    boolean hasPassword;
    String shareUrl;
}
