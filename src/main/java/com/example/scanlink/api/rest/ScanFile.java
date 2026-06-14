package com.example.scanlink.api.rest;

import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.handler.NotFoundException;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.FileShare;
import com.example.scanlink.api.model.enums.PermissionRole;
import com.example.scanlink.api.model.enums.Visibility;
import com.example.scanlink.api.service.interfaces.FileService;
import com.example.scanlink.api.service.OcrService;
import com.example.scanlink.api.service.interfaces.FileShareService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.scanlink.api.service.interfaces.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ScanFile {
    private final OcrService ocrService;
    private final CloudinaryService cloudinaryService;
    private final FileService fileService;
    private final FileShareService fileShareService;


// chưa test lỗi Authorized của Firebase
    @PostMapping("/getLinkCloudinary")
    public ResponseEntity<?> getLinkCloudinary(
            @RequestParam("file") MultipartFile file) throws IOException {

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }
            Map uploadResult = cloudinaryService.uploadFile(file.getBytes(), "scanlink/uploads");

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "fileName", file.getOriginalFilename(),
                            "publicId", uploadResult.get("public_id"),
                            "url", uploadResult.get("url"),
                            "secureUrl", uploadResult.get("secure_url"),
                            "resourceType", uploadResult.get("resource_type"),
                            "format", uploadResult.get("format"),
                            "bytes", uploadResult.get("bytes")
                    )
            );


    }

    // Đã test postman
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody UploadFileRequest request) {
            FileCommon savedFile = fileService.saveFile(request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "fileId", savedFile.getId(),
                    "message", "Lưu file thành công"
            ));

    }
    // đã test postman
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam String userId) {
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
    // chưa test được thiếu Tìm kiếm thông tin services
    @GetMapping("/shareWithMe")
    public ResponseEntity<?> shareWithMe(@RequestParam String userId) {
         List<SharedWithMeResponse> list =   fileShareService.getSharedWithMe(userId);
         if(list.isEmpty()) throw new NotFoundException("Không có file nào được share");
         return ResponseEntity.ok(Map.of("success",true,"total",list.size(),"data",list));

    }

    @PostMapping("/share")
    public ResponseEntity<?> shareFile(@RequestBody ShareFileRequest request) {
            FileShare result = fileShareService.shareFile(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "shareId", result.getId(),
                    "message", request.getVisibility() == Visibility.PUBLIC
                            ? "Đã share public thành công"
                            : "Đã share cho user thành công"));

    }
    @PutMapping("/share/permission")
    public ResponseEntity<?> updatePermission(@RequestBody UpdatePermissionRequest permissionRole) {
            FileShare result = fileShareService.updatePermission(permissionRole);
            return ResponseEntity.ok(Map.of(
                    "success",true,"message","Đã cập nhật quyền "+result.getRole()+" thành công"
            ));

    }
    @PutMapping("/share/visibility")
    public ResponseEntity<?> updateVisibility(@RequestBody UpdateVisibilityRequest visibilityRequest) {
        FileShare result = fileShareService.updateVisibility(visibilityRequest);
        return ResponseEntity.ok(Map.of(
                "success",true,"message","Đã cập nhật quyền "+result.getRole()+" thành công"
        ));

    }
}
