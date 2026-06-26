package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.sharefile.dao.FileRespository;
import com.example.scanlink.api.features.sharefile.dao.FileShareRespository;
import com.example.scanlink.api.features.sharefile.dto.FileHistoryResponse;
import com.example.scanlink.api.features.sharefile.dto.UploadFileRequest;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.FileType;
import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.ProcessingStatus;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileService;
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
public class FileServiceImp implements FileService {


    private final FileRespository fileRepository;
    private final FileShareRespository fileShareRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Document saveFile(UploadFileRequest request) {

        Document file = new Document();
        file.setTitle(request.getFileName());
        file.setFileSize(request.getSize());
        file.setOwnerUid(request.getUserId());
        file.setStorageUrl(request.getFileUrl());
        file.setCloudinaryPublicId(request.getCloudinaryPublicId());
        file.setStatus(ProcessingStatus.SUCCESS);
        file.setCreatedAt(LocalDateTime.now());
        file.setVisibility(Visibility.PRIVATE);
        return fileRepository.save(file);
    }
    @Override
    public List<FileHistoryResponse> getFilesByUserId(String userId) {
        return fileRepository.findByUserId(userId)
                .stream()
                .map(file -> new FileHistoryResponse(
                        file.getId(),
                        file.getTitle(),
                        file.getStorageUrl(),
                        file.getCreatedAt(),
                        PermissionRole.All,
                        file.getVisibility()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<FileHistoryResponse> getFilesByFileName(String fileName) {
        List<Document> files =
                fileRepository.findByFileNameOrderByCreatedAtDesc(fileName);

        return files.stream()
                .map(file -> {

                    SharedLink share = fileShareRepository
                            .findByFileId(file.getId());

                    return new FileHistoryResponse(
                            file.getId(),
                            file.getTitle(),
                            file.getStorageUrl(),
                            file.getCreatedAt(),
                            PermissionRole.All,
                            file.getVisibility()
                    );
                })
                .toList();
    }

    @Override
    public Document uploadAndSave(MultipartFile file, String userId, String type) throws IOException {
        // upload file cloudinary
        Map uploadResult = cloudinaryService.uploadFile(file.getBytes(), "scanlink/uploads");
        String publicId = (String) uploadResult.get("publicId");

        try {
            Document entity = new Document();
            entity.setTitle(file.getOriginalFilename());
            entity.setFileSize(file.getSize());
            entity.setOwnerUid(userId);
            entity.setStorageUrl((String) uploadResult.get("url"));
            entity.setCloudinaryPublicId((String) uploadResult.get("public_id"));
            entity.setType(detectFileType(file));
            entity.setStatus(ProcessingStatus.SUCCESS);
            entity.setCreatedAt(LocalDateTime.now());

            return fileRepository.save(entity);
        }catch (Exception e) {
                cloudinaryService.deleteFile(publicId);
            throw new IllegalArgumentException("File upload failed");
        }
    }

    @Override
    public Document findByIdAndUserId(String fileId, String userId) {
        return fileRepository.findByIdAndUserId(fileId,userId) ;
    }

    @Override
    public void deleteByIdAndUserId(String fileId, String userId) {
        Document file =  fileRepository.findById(fileId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!file.getOwnerUid().equals(userId)){
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        file.setIsDeleted(true);
        file.setDeletedAt(LocalDateTime.now());
        fileRepository.save(file);
    }

    private FileType detectFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) throw new AppException(ErrorCode.NOT_FOUND);

        return switch (contentType) {
            case "application/pdf"    -> FileType.PDF;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> FileType.DOCX;
            case "text/plain"         -> FileType.TXT;
            case "image/jpeg", "image/png", "image/webp" -> FileType.IMAGE;
            default -> throw new IllegalArgumentException("File type không hỗ trợ: " + contentType);
        };
    }
}
