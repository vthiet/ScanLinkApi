package com.example.scanlink.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class SharePublicResponse {
    String hasToken;
    String documentId;
    LocalDateTime expiryDate;
    boolean hasPassword;
    String shareUrl;
}
