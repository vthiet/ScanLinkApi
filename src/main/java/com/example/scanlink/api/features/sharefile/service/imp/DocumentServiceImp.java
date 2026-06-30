package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dao.SharedLinkRepository;
import com.example.scanlink.api.features.sharefile.dto.DocumentHistoryResponse;
import com.example.scanlink.api.features.sharefile.dto.DocumentResponse;
import com.example.scanlink.api.features.sharefile.dto.FileUploadRequest;
import com.example.scanlink.api.features.sharefile.dto.UploadFileRequest;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.features.sharefile.service.interfaces.DocumentService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements DocumentService {


    private final DocumentRepository documentRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Document uploadAndSave(FileUploadRequest fileUploadRequest, String userId) throws IOException {
        if (userId == null) throw new AppException(ErrorCode.NOT_FOUND);

        String originalName = fileUploadRequest.getFile().getOriginalFilename();
        if (originalName == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Map uploadResult = cloudinaryService.uploadFile(fileUploadRequest.getFile());
        if (uploadResult == null) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        try {
            Document entity = new Document();
            entity.setOwnerUid(userId);
            entity.setTitle(fileUploadRequest.getTitle());
            entity.setStorageUrl(url);
            entity.setFileSize(fileUploadRequest.getFile().getSize());
            entity.setExtractedText(fileUploadRequest.getExtractedText());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setCloudinaryPublicId(publicId);
            entity.setResourceType(resolveResourceType(originalName));

            return documentRepository.save(entity);
        } catch (Exception e) {
            if (publicId != null) {
                cloudinaryService.delete(publicId);
            }
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public DocumentResponse  findByIdAndUserId(String fileId, String userId) {
        // 1. Validate input rỗng
        if (fileId == null || fileId.isBlank()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        // 2. Không tìm thấy document
        Document file = documentRepository.findByIdAndOwnerUid(fileId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // 3. File đã bị xóa mềm
        if (file.getIsDeleted()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        DocumentResponse documentResponse = new DocumentResponse(file.getId(),
                file.getOwnerUid(),
                file.getTitle(),
                file.getStorageUrl(),
                file.getFileSize(),
                file.getExtractedText()
                ,file.getCreatedAt(),
                file.getUpdatedAt());
        return documentResponse;
    }

    @Override
    public void deleteByIdAndUserId(String fileId, String userId) {
        Document file = documentRepository.findById(fileId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!file.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        file.setIsDeleted(true);
        file.setDeletedAt(LocalDateTime.now());
        documentRepository.save(file);
    }

    @Override
    public Page<DocumentHistoryResponse> getMyDocuments(String uid, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        return documentRepository
                .findByOwnerUidAndIsDeletedFalse(uid, pageable)
                .map(file -> DocumentHistoryResponse.builder()
                        .id(file.getId())
                        .title(file.getTitle())
                        .storageUrl(cloudinaryService.generateDownloadUrl(
                                file.getCloudinaryPublicId(),
                                file.getResourceType()))
                        .fileSize(file.getFileSize())
                        .createdAt(file.getCreatedAt())
                        .build());
    }

    @Override
    public String resolveResourceType(String name) {
        if (name == null) return "raw";

        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

        return switch (ext) {
            // Image
            case "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico" -> "image";
            // Video
            case "mp4", "avi", "mov", "mkv", "wmv", "flv", "webm" -> "video";
            // Raw (document, audio, others)
            default -> "raw";
        };
    }


}
