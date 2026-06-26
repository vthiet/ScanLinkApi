package com.example.scanlink.api.features.sharefile.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.scanlink.api.dto.ApiResponse;
import com.example.scanlink.api.features.sharefile.service.interfaces.ICloudinaryService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CloudinaryImp implements ICloudinaryService {
    private final Cloudinary cloudinary;

    @Value("${scanlink.storage.url_folder_cloudinary}")
    private String urlFolderCloudinary;

    // Upload any file type (image, video, raw documents)
    public Map uploadFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                        "upload_preset","ml_default",
                        "folder",urlFolderCloudinary,
                         "filename_override", originalFilename,
                        "unique_filename",true,
                        "use_filename",true
        ));
    }

    public String generateDownloadUrl(String publicId, String resourceType) {
        return cloudinary.url()
                .secure(true)
                .resourceType(resourceType)
                .transformation(new Transformation().flags("attachment"))
                .generate(publicId);
    }


    @Override
    public byte[] downloadFile(String url) throws IOException {

        URL fileUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        byte[] fileBytes;
        try (InputStream in = connection.getInputStream()) {
            fileBytes = in.readAllBytes();
        } finally {
            connection.disconnect();
        }

        return fileBytes;
    }
    @Override
    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", "raw",        // mặc định "raw" cho document
                "invalidate", true             // xóa cache CDN luôn
        ));
    }
}
