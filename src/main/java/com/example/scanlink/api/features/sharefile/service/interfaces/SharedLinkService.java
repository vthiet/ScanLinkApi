package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.DocumentPermission;
import com.example.scanlink.api.features.sharefile.model.SharedLink;
import com.example.scanlink.api.features.sharefile.model.enums.PermissionRole;

public interface SharedLinkService {

    CreatePublicResponse createSharePublic(String userId, CreatePublicRequest createPublicRequest);
    SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest);
    void addSharedLink(String documentId, SharedLink sharedLink);
    void addDocumentPermissionRole(String userId, DocumentPermission documentPermission);
    DocumentResponse accessPublicLink(String hashtoken, String password);
}
