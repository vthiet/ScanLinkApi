package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileStorageService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class FileStorageServiceImp implements FileStorageService {
    @Value("${scanlink.storage.url_folder_server}")
    private String URL_FOLDER;

    @Override
    public Document receiveFile(MultipartFile file,String userId,String title,String extractedText) {
        Document document = null;
        try {
            // Tạo folder nếu chưa có
            Path folderPath = Paths.get(URL_FOLDER);
            Files.createDirectories(folderPath);

            // Ghi file xuống
            Path destPath = folderPath.resolve(file.getOriginalFilename());
            file.transferTo(destPath);

             document = new Document();
            document.setTitle(title);
            document.setExtractedText(extractedText);
            document.setFileSize(file.getSize());
            document.setCreatedAt(LocalDateTime.now());
            document.setOwnerUid(userId);



        } catch (IOException e) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return document;
    }
}
