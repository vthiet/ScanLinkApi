package com.example.scanlink.api.features.sharefile.service.imp;

import com.cloudinary.utils.ObjectUtils;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CloudinaryImp implements CloudinaryService {
    private final com.cloudinary.Cloudinary cloudinaryClient;

    @Override
    public Map uploadFile(MultipartFile file) throws IOException {
        return cloudinaryClient.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
        );
    }

    @Override
    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        Map result = uploadFile(file);
        return (String) result.get("secure_url");
    }

    @Override
    public Map uploadFile(byte[]  fileBytes, String folder) throws IOException {
        return cloudinaryClient.uploader().upload(
                fileBytes,
                ObjectUtils.asMap("folder", folder,"resource_type", "raw")
        );
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        cloudinaryClient.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public String getFileUrl(String publicId) {
        return cloudinaryClient.url()
                .secure(true)
                .generate(publicId);
    }

}
