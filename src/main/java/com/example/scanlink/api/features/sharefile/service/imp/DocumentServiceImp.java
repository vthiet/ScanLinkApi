package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dao.SharedLinkRepository;
import com.example.scanlink.api.features.sharefile.dto.DocumentHistoryResponse;
import com.example.scanlink.api.features.sharefile.dto.UploadFileRequest;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.features.sharefile.service.interfaces.IDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements IDocumentService {


    private final DocumentRepository documentRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Document saveFile(UploadFileRequest request) {

        Document file = new Document();
        file.setTitle(request.getFileName());
        file.setFileSize(request.getSize());
        file.setOwnerUid(request.getUserId());
        file.setStorageUrl(request.getFileUrl());
        file.setCreatedAt(LocalDateTime.now());
        return documentRepository.save(file);
    }
    @Override
    public List<DocumentHistoryResponse> getFilesByUserId(String userId) {
        return documentRepository.findByOwnerUid(userId)
                .stream()
                .map(file -> new DocumentHistoryResponse(
                        file.getId(),
                        file.getTitle(),
                        file.getStorageUrl(),
                        file.getCreatedAt(),
                        PermissionRole.All,
                        determineVisibility(file)
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<DocumentHistoryResponse> getFilesByFileName(String fileName) {
        List<Document> files =
                documentRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(fileName);

        return files.stream()
                .map(file -> {

                    SharedLink share = sharedLinkRepository
                            .findByDocumentId(file.getId());

                    PermissionRole role = (share != null) ? share.getRole() : PermissionRole.NONE;

                    return new DocumentHistoryResponse(
                            file.getId(),
                            file.getTitle(),
                            file.getStorageUrl(),
                            file.getCreatedAt(),
                            role,
                            determineVisibility(file)
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Document uploadAndSave(MultipartFile file, String userId, String title, String extractText) throws IOException {
        // upload file cloudinary
        Map uploadResult = cloudinaryService.uploadFile(file.getBytes(), "scanlink/uploads");
        String publicId = (String) uploadResult.get("publicId");

        if(userId ==null) throw new AppException(ErrorCode.NOT_FOUND);
        try {
            Document entity = new Document();
            entity.setTitle(title);
            entity.setFileSize(file.getSize());
            entity.setOwnerUid(userId);
            entity.setStorageUrl((String) uploadResult.get("url"));
            entity.setCreatedAt(LocalDateTime.now());
            entity.setExtractedText(extractText);

            return documentRepository.save(entity);
        }catch (Exception e) {
                 cloudinaryService.deleteFile(publicId);
            throw new IllegalArgumentException("File upload failed");
        }
    }

    @Override
    public Document findByIdAndUserId(String fileId, String userId) {
        return documentRepository.findByIdAndOwnerUid(fileId,userId) ;
    }

    @Override
    public void deleteByIdAndUserId(String fileId, String userId) {
        Document file =  documentRepository.findById(fileId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!file.getOwnerUid().equals(userId)){
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        file.setIsDeleted(true);
        file.setDeletedAt(LocalDateTime.now());
        documentRepository.save(file);
    }

    private Visibility determineVisibility(Document file) {
        if (file.getSharedLinks() != null && !file.getSharedLinks().isEmpty()) {
            return Visibility.PUBLIC;
        }
        if (file.getPermissions() != null && !file.getPermissions().isEmpty()) {
            return Visibility.SHARED;
        }
        SharedLink share = sharedLinkRepository.findByDocumentId(file.getId());
        if (share != null) {
            return share.getShareWithUserId() == null ? Visibility.PUBLIC : Visibility.SHARED;
        }
        return Visibility.PRIVATE;
    }
}
