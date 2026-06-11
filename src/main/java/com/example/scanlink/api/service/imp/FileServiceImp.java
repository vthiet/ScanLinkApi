package com.example.scanlink.api.service.imp;

import com.example.scanlink.api.dao.FileRespository;
import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.dto.UploadFileRequest;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.enums.FileType;
import com.example.scanlink.api.model.enums.PermissionRole;
import com.example.scanlink.api.model.enums.ProcessingStatus;
import com.example.scanlink.api.model.enums.Visibility;
import com.example.scanlink.api.service.interfaces.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService {


    private final FileRespository fileRepository;

    @Override
    public FileCommon saveFile(UploadFileRequest request) {

        FileCommon file = new FileCommon();
        file.setFileName(request.getFileName());
        file.setSize(request.getSize());
        file.setUserId(request.getUserId());
        file.setFileUrl(request.getFileUrl());
        file.setCloudinaryPublicId(request.getCloudinaryPublicId());
        file.setType(FileType.valueOf(request.getType()));
        file.setStatus(ProcessingStatus.SUCCESS);
        file.setCreatedAt(LocalDateTime.now());

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
                        PermissionRole.NONE,
                        Visibility.PRIVATE
                ))
                .collect(Collectors.toList());
    }
}
