package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.sharefile.dao.FileRespository;
import com.example.scanlink.api.features.sharefile.dao.FileShareRespository;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.handler.ForbiddenException;
import com.example.scanlink.api.handler.NotFoundException;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.example.scanlink.api.features.sharefile.service.interfaces.FileShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public SharedLink shareFile(ShareFileRequest request) {
        Document file = fileRespository.findById(request.getFileId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!file.getOwnerUid().equals(request.getOwnerUserId())){
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        SharedLink share = SharedLink.builder()
                .fileId(request.getFileId())
                .shareWithUserId(request.getTargetUserId())
                .role(request.getRole())
                .shareAt(LocalDateTime.now())
                .build();
        return fileShareRespository.save(share);
    }



    @Override
    public SharePublicResponse createSharePublic(String userId, SharePublicRequest sharePublicRequest) {
        boolean isOwner;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(sharePublicRequest.getPassword());
        sharePublicRequest.setPassword(hashed);

        Document fileCommon = fileRespository.findById(sharePublicRequest.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (fileCommon.getOwnerUid() != null) {
            isOwner = fileCommon.getOwnerUid().equals(userId);
        } else {
            isOwner = fileCommon.getGuestId() != null && fileCommon.getGuestId().equals(userId);
        }

        if (!isOwner) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        SharedLink fileshare = fileShareRespository.findByFileId(sharePublicRequest.getDocumentId());
        if(!sharePublicRequest.getPassword().isEmpty()){
            fileshare.setHasPassword(true);
            fileshare.setHashToken(sharePublicRequest.getPassword());
        }
        fileshare.setExpiryDate(fileshare.getExpiryDate().plusDays(sharePublicRequest.getExpireInDays()));
        fileshare.setRole(sharePublicRequest.getPermissionRole());
        fileCommon.setVisibility(Visibility.PUBLIC);
        fileShareRespository.save(fileshare);
        fileRespository.save(fileCommon);

        // đang share bằng link lưu file gốc ở Cloudinary
        SharePublicResponse res = new SharePublicResponse(fileshare.getHashToken(),
                fileshare.getFileId(),
                fileshare.getExpiryDate(),
                fileshare.isHasPassword(),
                fileCommon.getStorageUrl());
        return res;
    }

    @Override
    public SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest) {
        boolean isOwner;
        Document fileCommon = fileRespository.findById(sharePrivateRequest.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (fileCommon.getOwnerUid() != null) {
            isOwner = fileCommon.getOwnerUid().equals(userId);
        } else {
            isOwner = fileCommon.getGuestId() != null && fileCommon.getGuestId().equals(userId);
        }

        if (!isOwner) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        SharedLink fileshare = fileShareRespository.findByFileId(sharePrivateRequest.getDocumentId());
        // find email để tìm ra user id
        //... code tại đây

        fileshare.setRole(sharePrivateRequest.getPermissionRole());
        fileShareRespository.save(fileshare);


        return new SharePrivateResponse(sharePrivateRequest.getDocumentId(),sharePrivateRequest.getShareToEmail(),sharePrivateRequest.getPermissionRole());
    }
}

