package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dao.SharedLinkRepository;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.DocumentPermission;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.model.enums.Visibility;
import com.example.scanlink.api.features.sharefile.service.interfaces.ISharedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SharedLinkServiceImp implements ISharedLink {
    private final SharedLinkRepository sharedLinkRepository;
    private final DocumentRepository documentRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<SharedWithMeResponse> getSharedWithMe(String userId) {
        return sharedLinkRepository.findByShareWithUserIdOrderByShareAtDesc(userId)
                .stream()
                .map(share -> {
                    Document file = documentRepository.findById(share.getDocumentId())
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

                    return new SharedWithMeResponse(
                            file.getId(),
                            file.getTitle(),
                            file.getStorageUrl(),
                            file.getOwnerUid(),
                            share.getRole(),
                            share.getShareAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public SharedLink shareFile(ShareFileRequest request) {
        Document file = documentRepository.findById(request.getFileId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!file.getOwnerUid().equals(request.getOwnerUserId())){
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        SharedLink share = SharedLink.builder()
                .hashToken(java.util.UUID.randomUUID().toString())
                .documentId(request.getFileId())
                .shareWithUserId(request.getTargetUserId())
                .role(request.getRole())
                .shareAt(LocalDateTime.now())
                .build();
        
        SharedLink savedShare = sharedLinkRepository.save(share);

        // Sync nested/embedded sharedLinks in Document
        List<SharedLink> links = file.getSharedLinks();
        if (links == null) {
            links = new java.util.ArrayList<>();
        }
        links.add(savedShare);
        file.setSharedLinks(links);
        documentRepository.save(file);

        return savedShare;
    }

    @Override
    public SharePublicResponse createSharePublic(String userId, SharePublicRequest sharePublicRequest) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(sharePublicRequest.getPassword());
        sharePublicRequest.setPassword(hashed);

        Document fileCommon = documentRepository.findById(sharePublicRequest.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (fileCommon.getOwnerUid() == null || !fileCommon.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        SharedLink fileshare = sharedLinkRepository.findByDocumentId(sharePublicRequest.getDocumentId());
        if(fileshare == null) {
            fileshare = new SharedLink();
            fileshare.setDocumentId(sharePublicRequest.getDocumentId());
            fileshare.setShareAt(LocalDateTime.now());
            fileshare.setExpiryDate(LocalDateTime.now());
            fileshare.setHashToken(java.util.UUID.randomUUID().toString());
        }

        if(!sharePublicRequest.getPassword().isEmpty()){
            fileshare.setHasPassword(true);
            fileshare.setHashToken(sharePublicRequest.getPassword());
        }
        if(fileshare.getExpiryDate() == null) {
            fileshare.setExpiryDate(LocalDateTime.now());
        }
        fileshare.setExpiryDate(fileshare.getExpiryDate().plusDays(sharePublicRequest.getExpireInDays()));
        fileshare.setRole(sharePublicRequest.getPermissionRole());
        
        sharedLinkRepository.save(fileshare);

        // Sync nested/embedded sharedLinks in Document
        List<SharedLink> links = fileCommon.getSharedLinks();
        if (links == null) {
            links = new java.util.ArrayList<>();
        }
        links.removeIf(l -> fileCommon.getId().equals(l.getDocumentId()) && l.getShareWithUserId() == null);
        links.add(fileshare);
        fileCommon.setSharedLinks(links);
        documentRepository.save(fileCommon);

        SharePublicResponse res = new SharePublicResponse(
                fileshare.getHashToken(),
                fileshare.getDocumentId(),
                fileshare.getExpiryDate(),
                fileshare.isHasPassword(),
                fileCommon.getStorageUrl()
        );
        return res;
    }

    @Override
    public SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest) {
        Document fileCommon = documentRepository.findById(sharePrivateRequest.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (fileCommon.getOwnerUid() == null || !fileCommon.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // find email to find user ID
        Query query = new Query(Criteria.where("email").is(sharePrivateRequest.getShareToEmail()));
        UserEntity collaborator = mongoTemplate.findOne(query, UserEntity.class);
        if (collaborator == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        SharedLink fileshare = sharedLinkRepository.findByDocumentIdAndShareWithUserId(
                sharePrivateRequest.getDocumentId(), collaborator.getUid());
        if (fileshare == null) {
            fileshare = new SharedLink();
            fileshare.setDocumentId(sharePrivateRequest.getDocumentId());
            fileshare.setShareWithUserId(collaborator.getUid());
            fileshare.setShareAt(LocalDateTime.now());
            fileshare.setHashToken(java.util.UUID.randomUUID().toString());
        }
        fileshare.setRole(sharePrivateRequest.getPermissionRole());
        sharedLinkRepository.save(fileshare);

        // Sync nested/embedded permissions in Document
        List<DocumentPermission> permissions = fileCommon.getPermissions();
        if (permissions == null) {
            permissions = new java.util.ArrayList<>();
        }
        DocumentPermission perm = permissions.stream()
                .filter(p -> collaborator.getUid().equals(p.getUser_uid()))
                .findFirst()
                .orElse(null);
        if (perm == null) {
            perm = new DocumentPermission();
            perm.setUser_uid(collaborator.getUid());
            permissions.add(perm);
        }
        perm.setRole(sharePrivateRequest.getPermissionRole());
        fileCommon.setPermissions(permissions);

        // Sync nested/embedded sharedLinks in Document
        List<SharedLink> links = fileCommon.getSharedLinks();
        if (links == null) {
            links = new java.util.ArrayList<>();
        }
        links.removeIf(l -> fileCommon.getId().equals(l.getDocumentId()) && collaborator.getUid().equals(l.getShareWithUserId()));
        links.add(fileshare);
        fileCommon.setSharedLinks(links);

        documentRepository.save(fileCommon);

        return new SharePrivateResponse(
                sharePrivateRequest.getDocumentId(),
                sharePrivateRequest.getShareToEmail(),
                sharePrivateRequest.getPermissionRole()
        );
    }
}

