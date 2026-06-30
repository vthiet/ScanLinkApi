package com.example.scanlink.api.features.sharefile.service.interfaces;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map uploadFile(MultipartFile file) throws IOException;
    String generateDownloadUrl(String publicId, String extension);
    void delete(String publicId) throws IOException;
    byte[] downloadFile(String url) throws IOException;
    String generateDownloadUrlSecure(String publicId, String extension, int expire) throws Exception;
}
