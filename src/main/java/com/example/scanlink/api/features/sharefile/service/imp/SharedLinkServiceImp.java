package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dao.SharedLinkRepository;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.model.DocumentPermission;
import com.example.scanlink.api.features.sharefile.service.interfaces.SharedLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Override
    public CreatePublicResponse createSharePublic(String userId, CreatePublicRequest request) {

        Document docs = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!docs.getOwnerUid().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        List<SharedLink> links = docs.getSharedLinks();
        if (links == null) links = new ArrayList<>();

        SharedLink fileshare = links.stream()
                .filter(l -> l.getShareWithUserId() == null)
                .findFirst()
                .orElse(null);

        if (fileshare == null) {
            fileshare = SharedLink.builder()
                    .hashToken(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now())
                    .build();
            links.add(fileshare);
        }

        String rawPassword = request.getPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            fileshare.setHasPassword(true);
            fileshare.setPasswordHash(passwordEncoder.encode(rawPassword));
        } else {
            fileshare.setHasPassword(false);
            fileshare.setPasswordHash(null);
        }

        fileshare.setExpiresAt(LocalDateTime.now().plusDays(request.getExpireInDays()));

        String shareUrl;
        try {
            shareUrl = cloudinaryService.generateDownloadUrlSecure(
                    docs.getCloudinaryPublicId(),
                    docs.getResourceType(),
                    request.getExpireInDays()
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }

        docs.setSharedLinks(links);
        documentRepository.save(docs);

        CreatePublicResponse res = new CreatePublicResponse();
        res.setHashToken(fileshare.getHashToken());
        res.setDocumentId(request.getDocumentId());
        res.setExpiresAt(fileshare.getExpiresAt());
        res.setHasPassword(fileshare.isHasPassword());
        res.setShareUrl(shareUrl);
        return res;
    }

    @Override
    public SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest request) {
        return null;
    }
}

