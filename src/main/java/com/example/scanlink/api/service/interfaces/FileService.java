package com.example.scanlink.api.service.interfaces;

import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.dto.UploadFileRequest;
import com.example.scanlink.api.model.FileCommon;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileService {
     FileCommon saveFile(UploadFileRequest request);
     List<FileHistoryResponse> getFilesByUserId(String userId);
     List<FileHistoryResponse> getFilesByFileName(String fileName);
     FileCommon uploadAndSave(MultipartFile file, String userId,String type) throws IOException;
    FileCommon findByIdAndUserId(String fileId, String userId);
    void deleteByIdAndUserId(String fileId, String userId);
    }
