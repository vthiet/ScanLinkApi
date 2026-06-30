package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import com.example.scanlink.api.features.authentication.service.impl.UserServiceImpl;
import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.DocumentPermission;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.SharedLinkService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SharedLinkServiceImp implements SharedLinkService {
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final MongoTemplate mongoTemplate;
    private final UserServiceImpl userService;

    // permission userUUID sẽ là NULL
    // quyền mặc định là View
    @Override
    public CreatePublicResponse createSharePublic(String userId, CreatePublicRequest request) {
        Document docs = documentRepository.findById(request.getDocumentId()).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        if (!docs.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        SharedLink sharedLink = new SharedLink();
        sharedLink.setHashToken(UUID.randomUUID().toString());
        sharedLink.setExpiresAt(LocalDateTime.now().plusDays(request.getExpireInDays()));
        sharedLink.setCreatedAt(LocalDateTime.now());

        if(request.getPassword() != null){
           String pass= passwordEncoder.encode(request.getPassword());
           sharedLink.setPasswordHash(pass);
           sharedLink.setHasPassword(true);
        }else{
            sharedLink.setHasPassword(false);
        }

        DocumentPermission permissionRole = new DocumentPermission();
        permissionRole.setRole(PermissionRole.VIEW);
        permissionRole.setUser_uid(null);

        addSharedLink(request.getDocumentId(), sharedLink);
        addDocumentPermissionRole(request.getDocumentId(), permissionRole);
        String urlPublic =null;
        documentRepository.save(docs);
        try {
             urlPublic = cloudinaryService.generateDownloadUrlSecure(docs.getCloudinaryPublicId(),docs.getResourceType(),request.getExpireInDays());
        }catch (Exception e){
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
        sharedLink.setShareUrl(urlPublic);

        CreatePublicResponse res = new CreatePublicResponse();
        res.setHashToken(sharedLink.getHashToken());
        res.setDocumentId(docs.getId());
        res.setExpiresAt(sharedLink.getExpiresAt());
        res.setShareUrl(urlPublic);
        return res;
    }

    @Override
    public SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest request) {
        // tìm document
        Document docs = documentRepository.findById(request.getDocumentId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!docs.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        SharedLink sharedLink = new SharedLink();
        sharedLink.setHashToken(UUID.randomUUID().toString());
        sharedLink.setCreatedAt(LocalDateTime.now());
        sharedLink.setExpiresAt(null);
        sharedLink.setHasPassword(false);


        DocumentPermission permissionRole = new DocumentPermission();
        permissionRole.setRole(request.getPermissionRole());

        // ko tìm thấy user ? user == null ?
        UserEntity user =userService.findByEmail(request.getShareToEmail());
        System.out.println("user: " + user); // null hay có data?

        if(user == null){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        permissionRole.setUser_uid(user.getUid());

        addSharedLink(request.getDocumentId(), sharedLink);
        addDocumentPermissionRole(request.getDocumentId(), permissionRole);


        SharePrivateResponse res = new SharePrivateResponse();
        res.setDocumentId(request.getDocumentId());
        res.setCollaboratorEmail(request.getShareToEmail());
        res.setPermissionRole(request.getPermissionRole());
        return res;
    }

    @Override
    public void addSharedLink(String documentId, SharedLink sharedLink) {
        Query query = Query.query(Criteria.where("_id").is(documentId));

        Update update = new Update().push("sharedLinks", sharedLink);
        mongoTemplate.updateFirst(query,update,Document.class);
    }

    @Override
    public void addDocumentPermissionRole(String userId, DocumentPermission documentPermission) {
        Query query = Query.query(Criteria.where("ownerUid").is(userId));

        Update update = new Update().push("DOCUMENT_PERMiSSION", documentPermission);
        mongoTemplate.updateFirst(query,update,Document.class);
    }

    @Override
    public DocumentResponse accessPublicLink(String hashToken, String password) {

        // 1. Tìm Document chứa hashToken này
        Document docs = documentRepository.findBySharedLinksHashToken(hashToken)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // 2. Tìm đúng SharedLink trong list
        SharedLink sharedLink = docs.getSharedLinks().stream()
                .filter(l -> hashToken.equals(l.getHashToken()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // 3. Kiểm tra hết hạn
        if (sharedLink.getExpiresAt() != null &&
                sharedLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED);
        }

        // 4. Kiểm tra password nếu có
        if (sharedLink.isHasPassword()) {
            if (password == null || password.isBlank()) {
                throw new AppException(ErrorCode.UNAUTHORIZED); // chưa nhập password
            }
            if (!passwordEncoder.matches(password, sharedLink.getPasswordHash())) {
                throw new AppException(ErrorCode.UNAUTHORIZED); // sai password
            }
        }

        // 5. Generate URL để truy cập file
        String fileUrl;
        try {
            fileUrl = cloudinaryService.generateDownloadUrlSecure(
                    docs.getCloudinaryPublicId(),
                    docs.getResourceType(),
                    1 // 1 ngày
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }

        // 6. Build response
        DocumentResponse res = new DocumentResponse();
        res.setId(docs.getId());
        res.setTitle(docs.getTitle());
        res.setStorageUrl(fileUrl);
        res.setFileSize(docs.getFileSize());
        res.setCreatedAt(docs.getCreatedAt());
        return res;
    }
}

