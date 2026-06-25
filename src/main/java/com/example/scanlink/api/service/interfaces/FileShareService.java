package com.example.scanlink.api.service.interfaces;

import com.example.scanlink.api.dto.*;
import com.example.scanlink.api.model.FileShare;

import java.util.List;

public interface FileShareService {
    List<SharedWithMeResponse> getSharedWithMe(String userId);
    FileShare shareFile(ShareFileRequest request);

    SharePublicResponse createSharePublic(String userId, SharePublicRequest sharePublicRequest);
    SharePrivateResponse createSharePrivate(String userId, SharePrivateRequest sharePrivateRequest);

}
