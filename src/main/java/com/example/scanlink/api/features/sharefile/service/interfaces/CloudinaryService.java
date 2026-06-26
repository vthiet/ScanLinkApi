package com.example.scanlink.api.features.sharefile.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    /**
     * Upload file lên Cloudinary
     */
    Map uploadFile(MultipartFile file) throws IOException;

    /**
     * Upload và trả về URL
     */
    String uploadAndGetUrl(MultipartFile file) throws IOException;

    /**
     * Upload vào folder chỉ định
     */
    Map uploadFile(byte[]  fileBytes, String folder) throws IOException;

    /**
     * Xóa file theo publicId
     */
    void deleteFile(String publicId) throws IOException;

    /**
     * Lấy URL từ publicId
     */
    String getFileUrl(String publicId);

}
