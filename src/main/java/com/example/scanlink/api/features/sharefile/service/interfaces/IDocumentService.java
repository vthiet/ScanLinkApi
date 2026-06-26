package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.DocumentHistoryResponse;
import com.example.scanlink.api.features.sharefile.dto.FileUploadRequest;
import com.example.scanlink.api.features.sharefile.dto.UploadFileRequest;
import com.example.scanlink.api.features.sharefile.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IDocumentService {
     Document saveFile(UploadFileRequest request);
     List<DocumentHistoryResponse> getFilesByUserId(String userId);
     List<DocumentHistoryResponse> getFilesByFileName(String fileName);
     Document uploadAndSave(FileUploadRequest fileUploadRequest, String userId) throws IOException;
    Document findByIdAndUserId(String fileId, String userId);
    void deleteByIdAndUserId(String fileId, String userId);
    }
