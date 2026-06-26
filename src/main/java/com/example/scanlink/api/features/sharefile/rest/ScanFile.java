package com.example.scanlink.api.features.sharefile.rest;

import com.example.scanlink.api.authentication.FirebaseUserPrincipal;
import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileService;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileShareService;
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
    private final FileService fileService;
    private final FileShareService fileShareService;

    // Đã test postman
    @PostMapping("/documents")
    public ApiResponse<?> upload(@RequestParam("file") MultipartFile file,
                                    Authentication authentication,
                                    @RequestParam String type) throws IOException {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        String userId = principal.uid();
        Document saved = fileService.uploadAndSave(file, userId, type);

        return ApiResponse.success(saved);
    }

    // đã test postman
    @GetMapping("/documents")
    public ApiResponse<?> getHistory(Authentication authentication) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        String userId = principal.uid();
        if(userId == null) throw new IllegalArgumentException("userid Cannot empty");
        List<FileHistoryResponse> files = fileService.getFilesByUserId(userId);
        return ApiResponse.success(files);
    }

    // soft delete
    @GetMapping("/documents/{id}")
    public ApiResponse<?> getDocumentById(Authentication authentication,@PathVariable String id) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
       Document file = fileService.findByIdAndUserId(id,principal.uid());
        return ApiResponse.success(file);

    }
    // soft delete
    @DeleteMapping("/documents/{id}")
    public ApiResponse<?> deleteDocumentById(Authentication authentication,@PathVariable String id) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
       fileService.deleteByIdAndUserId(id,principal.uid());
        return ApiResponse.success(null);
    }
    @PostMapping("/shares/public")
    public ApiResponse<?> getPublicShares(Authentication authentication, SharePublicRequest sharePublicRequest) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
       SharePublicResponse res= fileShareService.createSharePublic(principal.uid(), sharePublicRequest);

        return ApiResponse.success(res);
    }

    @PostMapping("/shares/private")
    public ApiResponse<?> getPrivateShares(Authentication authentication, SharePrivateRequest sharePrivateRequest) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        SharePrivateResponse res= fileShareService.createSharePrivate(principal.uid(), sharePrivateRequest);

        return ApiResponse.success(res);
    }

//    @GetMapping("/shares/public/{pwd}")
//    public ResponseEntity<?> getPublicShares(Authentication authentication, @PathVariable String pwd) {
//        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        return ResponseEntity.ok(Map.of("status","success","message","Phân quyền thành công","data",res));
//
//    }

    // chưa test được thiếu Tìm kiếm thông tin services
    @GetMapping("/shareWithMe")
    public ApiResponse<?> shareWithMe(Authentication authentication) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();

        List<SharedWithMeResponse> list =   fileShareService.getSharedWithMe(principal.uid());
         if(list.isEmpty()) throw new AppException(ErrorCode.NOT_FOUND);
         return ApiResponse.success(list);

    }


}
