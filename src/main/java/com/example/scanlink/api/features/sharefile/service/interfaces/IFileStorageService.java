package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IFileStorageService {
    Document receiveFile(MultipartFile file, String userId, String title, String extractedText);
}
