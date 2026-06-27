package com.example.scanlink.api.features.sharefile.rest.document;

import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileStorageService;
import com.example.scanlink.api.features.sharefile.service.interfaces.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DocumentController {
    private final DocumentService documentservice;
    private final FileStorageService fileStorageService;
    private final CloudinaryService cloudinaryService;



    //SRĐ: 5.3.2 API 003
    @PostMapping("/documents")
    public ApiResponse<?> upload(FileUploadRequest fileUploadRequest, Authentication authentication) throws IOException {
        String userId = (String) authentication.getPrincipal();
       Document saved = documentservice.uploadAndSave(fileUploadRequest,userId);

        return ApiResponse.success(saved);
    }

    // chưa test
    @PostMapping("/write/documents")
    public ApiResponse<?> writeDownServer(@RequestParam("file") MultipartFile file,
                                 Authentication authentication,
                                 @RequestParam String title,@RequestParam String extractText) throws IOException {
        String userId = (String) authentication.getPrincipal();
        Document saved = fileStorageService.receiveFile(file, userId, title,extractText);

        return ApiResponse.success(saved);
    }

    // SRĐ: 5.3.2 API 004
    @GetMapping("/documents")
    public ApiResponse<?> getMyDocument(Authentication authentication,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(defaultValue = "createdAt") String sortBy) throws Exception {
        String userId = (String) authentication.getPrincipal();
        if(userId == null) throw new IllegalArgumentException("userid Cannot empty");
        Page<DocumentHistoryResponse> result = documentservice.getMyDocuments(userId, page, size, sortBy);

        return ApiResponse.success(PageResponse.of(result));
    }

    // SRĐ: 5.3.2 API 005
    // soft delete
    @GetMapping("/documents/{id}")
    public ApiResponse<?> getDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        DocumentResponse file = documentservice.findByIdAndUserId(id, userId);
        return ApiResponse.success(file);
    }

    // SRĐ: 5.3.2 API 006
    // soft delete
    @DeleteMapping("/documents/{id}")
    public ApiResponse<?> deleteDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        documentservice.deleteByIdAndUserId(id, userId);
        return ApiResponse.successDelete(null);
    }


}
