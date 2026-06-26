package com.example.scanlink.api.features.sharefile.rest.document;

import com.cloudinary.utils.ObjectUtils;
import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.ICloudinaryService;
import com.example.scanlink.api.features.sharefile.service.interfaces.IFileStorageService;
import com.example.scanlink.api.features.sharefile.service.interfaces.IDocumentService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DocumentController {
    private final IDocumentService documentservice;
    private final IFileStorageService fileStorageService;
    private final ICloudinaryService cloudinaryService;



    // Đã test postman
    @PostMapping("/documents")
    public ApiResponse<?> upload(FileUploadRequest fileUploadRequest, Authentication authentication) throws IOException {
        String userId = (String) authentication.getPrincipal();
       Document saved = documentservice.uploadAndSave(fileUploadRequest,userId);

        return ApiResponse.success(saved);
    }
    @GetMapping("/documents/{id}/download")
    public ApiResponse<?> downloadDocument(
            @PathVariable String id,
            Authentication authentication) throws IOException {
//
//        String userId = (String) authentication.getPrincipal();
//        Document doc = documentservice.findByIdAndUserId(id, userId);
//        if (doc == null) throw new AppException(ErrorCode.NOT_FOUND);
//
//        String fileName = doc.getOriginalFilename();
//        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
//
//        String publicId = doc.getCloudinaryPublicId();
//        if (publicId == null || publicId.isEmpty()) throw new AppException(ErrorCode.NOT_FOUND);
//
//        String downloadUrl = cloudinaryService.downloadFile(publicId,resolveExtension(ext));
//        return ApiResponse.success(downloadUrl);
        return ApiResponse.error("Chưa hoàn thành");
    }
    private String resolveExtension(String ext) {
        if(ext == null) throw new AppException(ErrorCode.BAD_REQUEST);

        switch (ext) {
            // Nhóm định dạng Ảnh
            case "jpg": case "jpeg": case "png": case "gif": case "webp": case "svg": case "bmp":
                return "image";

            // Nhóm định dạng Video & Audio
            case "mp4": case "avi": case "mov": case "mkv": case "mp3": case "wav": case "flac":
                return "video";

            // Tất cả tài liệu, file nén... còn lại đưa vào raw
            default:
                return "raw";
        }
    }

    @PostMapping("/write/documents")
    public ApiResponse<?> writeDownServer(@RequestParam("file") MultipartFile file,
                                 Authentication authentication,
                                 @RequestParam String title,@RequestParam String extractText) throws IOException {
        String userId = (String) authentication.getPrincipal();
        Document saved = fileStorageService.receiveFile(file, userId, title,extractText);

        return ApiResponse.success(saved);
    }

    // đã test postman
    @GetMapping("/documents")
    public ApiResponse<?> getHistory(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        if(userId == null) throw new IllegalArgumentException("userid Cannot empty");
        List<DocumentHistoryResponse> files = documentservice.getFilesByUserId(userId);
        return ApiResponse.success(files);
    }

    // soft delete
    @GetMapping("/documents/{id}")
    public ApiResponse<?> getDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        com.example.scanlink.api.features.sharefile.model.Document file = documentservice.findByIdAndUserId(id, userId);
        return ApiResponse.success(file);
    }

    // soft delete
    @DeleteMapping("/documents/{id}")
    public ApiResponse<?> deleteDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        documentservice.deleteByIdAndUserId(id, userId);
        return ApiResponse.success(null);
    }


}
