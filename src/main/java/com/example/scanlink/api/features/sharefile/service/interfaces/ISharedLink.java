package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.model.SharedLink;

import java.util.List;

public interface ISharedLink {
    List<SharedWithMeResponse> getSharedWithMe(String userId);
    SharedLink shareFile(ShareFileRequest request);

    SharePublicResponse createSharePublic(String userId, CreatePublicShareRequest createPublicShareRequest);
    SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest);

}
