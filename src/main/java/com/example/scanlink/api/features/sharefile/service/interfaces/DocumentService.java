package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.DocumentHistoryResponse;
import com.example.scanlink.api.features.sharefile.dto.DocumentResponse;
import com.example.scanlink.api.features.sharefile.dto.FileUploadRequest;
import com.example.scanlink.api.features.sharefile.dto.UploadFileRequest;
import com.example.scanlink.api.features.sharefile.model.Document;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {

     Document uploadAndSave(FileUploadRequest fileUploadRequest, String userId) throws IOException;
    DocumentResponse  findByIdAndUserId(String fileId, String userId);
    void deleteByIdAndUserId(String fileId, String userId);
    Page<DocumentHistoryResponse> getMyDocuments(String uid, int page, int size, String sortBy);
    String resolveResourceType(String name);
    }
