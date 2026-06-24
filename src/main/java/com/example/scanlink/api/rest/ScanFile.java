package com.example.scanlink.api.rest;

import com.example.scanlink.api.authentication.FirebaseUserPrincipal;
import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.handler.NotFoundException;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.FileShare;
import com.example.scanlink.api.model.enums.Visibility;
import com.example.scanlink.api.service.interfaces.FileService;
import com.example.scanlink.api.service.interfaces.FileShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import com.example.scanlink.api.service.interfaces.CloudinaryService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ScanFile {
    private final CloudinaryService cloudinaryService;
    private final FileService fileService;
    private final FileShareService fileShareService;

    // Đã test postman
    @PostMapping("/documents")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    Authentication authentication,
                                    @RequestParam String type) throws IOException {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        String userId = principal.uid();
        FileCommon saved = fileService.uploadAndSave(file, userId, type);
        return ResponseEntity.ok(Map.of("success", true, "fileId", saved.getId()));
    }

    // đã test postman
    @GetMapping("/documents")
    public ResponseEntity<?> getHistory(Authentication authentication) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        String userId = principal.uid();
        if(userId == null) throw new IllegalArgumentException("userid Cannot empty");
        List<FileHistoryResponse> files = fileService.getFilesByUserId(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "total", files.size(),
                    "data", files.stream()  .map(f -> Map.of(
                                    "fileId", f.getFileId(),
                                    "fileName", f.getFileName(),
                                    "fileUrl",f.getFileUrl(),
                                        "createdAt",f.getCreatedAt(),
                                    "permission", f.getPermissionRole(),
                                        "visibility",f.getVisibility()
                            ))
                            .collect(Collectors.toList())
            ));


    }

    // soft delete
    @GetMapping("/documents/{id}")
    public ResponseEntity<?> getDocumentById(Authentication authentication,@PathVariable String id) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        fileService.deleteByIdAndUserId(id,principal.uid());
        return ResponseEntity.ok(Map.of("status","success"));

    }
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDocumentById(Authentication authentication,@PathVariable String id) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
       fileService.deleteByIdAndUserId(id,principal.uid());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Xóa tài liệu thành công",
                "data", null
        ));
    }
    @PostMapping("/shares/public")
    public ResponseEntity<?> getPublicShares(Authentication authentication, SharePublicRequest sharePublicRequest) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
       SharePublicResponse res= fileShareService.createSharePublic(principal.uid(), sharePublicRequest);

        return ResponseEntity.ok(Map.of("status","success","message","Tạo liên kết thành công","data",res));
    }

    @PostMapping("/shares/private")
    public ResponseEntity<?> getPrivateShares(Authentication authentication, SharePrivateRequest sharePrivateRequest) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();
        SharePrivateResponse res= fileShareService.createSharePrivate(principal.uid(), sharePrivateRequest);

        return ResponseEntity.ok(Map.of("status","success","message","Phân quyền thành công","data",res));
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
    public ResponseEntity<?> shareWithMe(Authentication authentication) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal) authentication.getPrincipal();

        List<SharedWithMeResponse> list =   fileShareService.getSharedWithMe(principal.uid());
         if(list.isEmpty()) throw new NotFoundException("Không có file nào được share");
         return ResponseEntity.ok(Map.of("success",true,"total",list.size(),"data",list));

    }


}
