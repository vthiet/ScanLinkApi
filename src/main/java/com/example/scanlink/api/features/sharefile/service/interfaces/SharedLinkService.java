package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.*;

public interface SharedLinkService {

    CreatePublicResponse createSharePublic(String userId, CreatePublicRequest createPublicRequest);
    SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest);

}
