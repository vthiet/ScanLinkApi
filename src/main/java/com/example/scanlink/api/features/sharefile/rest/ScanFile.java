package com.example.scanlink.api.features.sharefile.rest;

import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.IDocumentService;
import com.example.scanlink.api.features.sharefile.service.interfaces.ISharedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ScanFile {
    private final CloudinaryService cloudinaryService;
    private final IDocumentService IDocumentService;
    private final ISharedLink ISharedLink;

    // Đã test postman
    @PostMapping("/documents")
    public ApiResponse<?> upload(@RequestParam("file") MultipartFile file,
                                 Authentication authentication,
                                 @RequestParam String title,@RequestParam String extractText) throws IOException {
        String userId = (String) authentication.getPrincipal();
        Document saved = IDocumentService.uploadAndSave(file, userId, title,extractText);

        return ApiResponse.success(saved);
    }

    // đã test postman
    @GetMapping("/documents")
    public ApiResponse<?> getHistory(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        if(userId == null) throw new IllegalArgumentException("userid Cannot empty");
        List<DocumentHistoryResponse> files = IDocumentService.getFilesByUserId(userId);
        return ApiResponse.success(files);
    }

    // soft delete
    @GetMapping("/documents/{id}")
    public ApiResponse<?> getDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        Document file = IDocumentService.findByIdAndUserId(id, userId);
        return ApiResponse.success(file);
    }

    // soft delete
    @DeleteMapping("/documents/{id}")
    public ApiResponse<?> deleteDocumentById(Authentication authentication, @PathVariable String id) {
        String userId = (String) authentication.getPrincipal();
        IDocumentService.deleteByIdAndUserId(id, userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/shares/public")
    public ApiResponse<?> getPublicShares(Authentication authentication, SharePublicRequest sharePublicRequest) {
        String userId = (String) authentication.getPrincipal();
        SharePublicResponse res = ISharedLink.createSharePublic(userId, sharePublicRequest);

        return ApiResponse.success(res);
    }

    @PostMapping("/shares/private")
    public ApiResponse<?> getPrivateShares(Authentication authentication, SharePrivateRequest sharePrivateRequest) {
        String userId = (String) authentication.getPrincipal();
        SharePrivateResponse res = ISharedLink.createSharePrivate(userId, sharePrivateRequest);

        return ApiResponse.success(res);
    }

    // chưa test được thiếu Tìm kiếm thông tin services
    @GetMapping("/shareWithMe")
    public ApiResponse<?> shareWithMe(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<SharedWithMeResponse> list = ISharedLink.getSharedWithMe(userId);
        if(list.isEmpty()) throw new AppException(ErrorCode.NOT_FOUND);
        return ApiResponse.success(list);
    }
}
