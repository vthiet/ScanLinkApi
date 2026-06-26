package com.example.scanlink.api.features.sharefile.service.interfaces;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.Map;

public interface ICloudinaryService {
    Map uploadFile(MultipartFile file) throws IOException;
    String generateDownloadUrl(String publicId, String extension);
    void delete(String publicId) throws IOException;
    byte[] downloadFile(String url) throws IOException;

}
