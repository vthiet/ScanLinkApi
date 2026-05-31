package com.example.scanlink.api.service.imp;

import com.example.scanlink.api.dao.FileRespository;
import com.example.scanlink.api.dao.FileShareRespository;
import com.example.scanlink.api.dto.SharedWithMeResponse;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.User;
import com.example.scanlink.api.service.interfaces.FileShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FileShareServiceImp implements FileShareService {
    private final FileShareRespository fileShareRespository;
    private final FileRespository fileRespository;

    @Override
    public List<SharedWithMeResponse> getSharedWithMe(String userId) {
//        return fileShareRespository.findBySharedWithUserIdOrderByShareAtDesc(userId)
//                .stream()
//                .map(share -> {
//                    FileCommon file = fileRespository.findById(share.getFileId())
//                            .orElseThrow(() -> new RuntimeException("File không tồn tại"));
//
//                    // lấy thông tin user
//
//                    return new SharedWithMeResponse(
//                            file.getId(),
//                            file.getFileName(),
//                            file.getFileUrl(),
//                            share.getShareName(),  // ← bỏ file.getUserId()
//                            share.getRole(),
//                            share.getShareAt()
//                    );
//                })
//                .collect(Collectors.toList());
        return null;
    }
}
