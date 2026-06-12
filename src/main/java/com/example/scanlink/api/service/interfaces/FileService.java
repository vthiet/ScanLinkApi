package com.example.scanlink.api.service.interfaces;

import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.dto.UploadFileRequest;
import com.example.scanlink.api.model.FileCommon;

import java.util.List;

public interface FileService {
    public FileCommon saveFile(UploadFileRequest request);
    public List<FileHistoryResponse> getFilesByUserId(String userId);
}
