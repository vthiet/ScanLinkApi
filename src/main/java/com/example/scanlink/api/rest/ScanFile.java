package com.example.scanlink.api.rest;

import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.dto.SharedWithMeResponse;
import com.example.scanlink.api.dto.UploadFileRequest;
import com.example.scanlink.api.model.FileCommon;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ScanFile {
    private final OcrService ocrService;
    private final CloudinaryService cloudinaryService;
    private final FileService fileService;
    private final FileShareService fileShareService;


    // chưa test
    @PostMapping("/scan")
    public ResponseEntity<?> scan(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File không được để trống");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Chỉ chấp nhận file ảnh");
        }

        File tempImage = null;
        File pdfFile = null;
        try {
            // Tạo file ảnh tạm
            String originalName = file.getOriginalFilename();
            String suffix = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf(".")) : ".png";
            tempImage = File.createTempFile("ocr_", suffix);
            file.transferTo(tempImage);

            // 1. OCR lấy text
            String extractedText = ocrService.extractText(tempImage);

            // 2. Tạo searchable PDF
            pdfFile = ocrService.createSearchablePdf(tempImage);

            // 3. Upload PDF lên Cloudinary
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            Map cloudinaryResult = cloudinaryService.uploadFile(pdfBytes, "scanlink/pdfs");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "text", extractedText,
                    "pdfUrl", cloudinaryResult.get("secure_url"),
                    "publicId", cloudinaryResult.get("public_id"),
                    "fileName", file.getOriginalFilename(),
                    "size", file.getSize()
            ));

        } catch (TesseractException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false, "error", "OCR thất bại: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false, "error", "Lỗi file: " + e.getMessage()));
        } finally {
            // Xóa cả 2 file tạm
            if (tempImage != null && tempImage.exists()) tempImage.delete();
            if (pdfFile != null && pdfFile.exists()) pdfFile.delete();
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody UploadFileRequest request) {
        try {
            FileCommon savedFile = fileService.saveFile(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "fileId", savedFile.getId(),
                    "message", "Lưu file thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Lưu file thất bại: " + e.getMessage()
            ));
        }
    }
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam String userId) {
        try {
            List<FileHistoryResponse> files = fileService.getFilesByUserId(userId);

            if (files.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Chưa có lịch sử scan",
                        "data", List.of()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "total", files.size(),
                    "data", files
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Lỗi lấy lịch sử: " + e.getMessage()
            ));
        }
    }
    @GetMapping("/shareWithMe")
    public ResponseEntity<?> shareWithMe(@RequestParam String userId) {
        try {
         List<SharedWithMeResponse> list =   fileShareService.getSharedWithMe(userId);
            if(list.isEmpty()) return  ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Không có file nào được chia sẻ",
                    "data", List.of()
            ));
            return ResponseEntity.ok(Map.of("success",true,"total",list.size(),"data",list));

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Không có file nào được share: " + e.getMessage()
            ));
        }

    }
}
