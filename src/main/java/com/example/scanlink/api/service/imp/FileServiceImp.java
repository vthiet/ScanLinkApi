package com.example.scanlink.api.service.imp;

import com.example.scanlink.api.dao.FileRespository;
import com.example.scanlink.api.dao.FileShareRespository;
import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.dto.UploadFileRequest;
import com.example.scanlink.api.handler.ForbiddenException;
import com.example.scanlink.api.handler.NotFoundException;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.FileShare;
import com.example.scanlink.api.model.enums.FileType;
import com.example.scanlink.api.model.enums.PermissionRole;
import com.example.scanlink.api.model.enums.ProcessingStatus;
import com.example.scanlink.api.model.enums.Visibility;
import com.example.scanlink.api.service.interfaces.CloudinaryService;
import com.example.scanlink.api.service.interfaces.FileService;
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
    public FileCommon saveFile(UploadFileRequest request) {

        FileCommon file = new FileCommon();
        file.setFileName(request.getFileName());
        file.setSize(request.getSize());
        file.setUserId(request.getUserId());
        file.setFileUrl(request.getFileUrl());
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
                        file.getFileName(),
                        file.getFileUrl(),
                        file.getCreatedAt(),
                        PermissionRole.All,
                        file.getVisibility()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<FileHistoryResponse> getFilesByFileName(String fileName) {
        List<FileCommon> files =
                fileRepository.findByFileNameOrderByCreatedAtDesc(fileName);

        return files.stream()
                .map(file -> {

                    FileShare share = fileShareRepository
                            .findByFileId(file.getId());

                    return new FileHistoryResponse(
                            file.getId(),
                            file.getFileName(),
                            file.getFileUrl(),
                            file.getCreatedAt(),
                            PermissionRole.All,
                            file.getVisibility()
                    );
                })
                .toList();
    }

    @Override
    public FileCommon uploadAndSave(MultipartFile file, String userId,String type) throws IOException {
        // upload file cloudinary
        Map uploadResult = cloudinaryService.uploadFile(file.getBytes(), "scanlink/uploads");
        String publicId = (String) uploadResult.get("publicId");

        try {
            FileCommon entity = new FileCommon();
            entity.setFileName(file.getOriginalFilename());
            entity.setSize(file.getSize());
            entity.setUserId(userId);
            entity.setFileUrl((String) uploadResult.get("url"));
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
    public FileCommon findByIdAndUserId(String fileId, String userId) {
        return fileRepository.findByIdAndUserId(fileId,userId) ;
    }

    @Override
    public void deleteByIdAndUserId(String fileId, String userId) {
        FileCommon file =  fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File not found"));
        if(!file.getUserId().equals(userId)){
            throw new ForbiddenException("Deny access");
        }
        file.setIsDeleted(true);
        file.setDeletedAt(LocalDateTime.now());
        fileRepository.save(file);
    }

    private FileType detectFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) throw new IllegalArgumentException("Không xác định được loại file");

        return switch (contentType) {
            case "application/pdf"    -> FileType.PDF;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> FileType.DOCX;
            case "text/plain"         -> FileType.TXT;
            case "image/jpeg", "image/png", "image/webp" -> FileType.IMAGE;
            default -> throw new IllegalArgumentException("File type không hỗ trợ: " + contentType);
        };
    }
}
