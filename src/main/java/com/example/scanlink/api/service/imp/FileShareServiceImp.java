package com.example.scanlink.api.service.imp;

import com.example.scanlink.api.dao.FileRespository;
import com.example.scanlink.api.dao.FileShareRespository;
import com.example.scanlink.api.dto.ShareFileRequest;
import com.example.scanlink.api.dto.SharedWithMeResponse;
import com.example.scanlink.api.dto.UpdatePermissionRequest;
import com.example.scanlink.api.model.FileCommon;
import com.example.scanlink.api.model.FileShare;
import com.example.scanlink.api.model.User;
import com.example.scanlink.api.service.interfaces.FileShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public FileShare shareFile(ShareFileRequest request) {
        FileCommon file = fileRespository.findById(request.getFileId())
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));
        if(!file.getUserId().equals(request.getOwnerUserId())){
            throw new RuntimeException("Chỉ OWNER mới được share file");
        }

        FileShare share = FileShare.builder()
                .fileId(request.getFileId())
                .shareWithUserId(request.getTargetUserId())
                .role(request.getRole())
                .visibility(request.getVisibility())
                .shareAt(LocalDateTime.now())
                .build();
        return fileShareRespository.save(share);
    }

    @Override
    public FileShare updatePermission(UpdatePermissionRequest request) {
        // lấy ra fileshare cần cập nhật
        FileShare share = fileShareRespository.findById(request.getFileShareId()).orElseThrow(()-> new RuntimeException("Share Không tồn tại"));

        // người đó phải chủ file không
        FileCommon file = fileRespository.findById(share.getFileId())
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));

        if(!file.getUserId().equals(request.getOwnerUserId())){
            throw new RuntimeException("Chỉ OWNER mới được đổi quyền");
        }
        // đặt lại quyền mới
        share.setRole(request.getNewRole());
        return fileShareRespository.save(share);
    }

}
