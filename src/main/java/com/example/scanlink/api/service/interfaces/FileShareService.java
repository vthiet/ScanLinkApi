package com.example.scanlink.api.service.interfaces;

import com.example.scanlink.api.dto.ShareFileRequest;
import com.example.scanlink.api.dto.SharedWithMeResponse;
import com.example.scanlink.api.dto.UpdatePermissionRequest;
import com.example.scanlink.api.dto.UpdateVisibilityRequest;
import com.example.scanlink.api.model.FileShare;

import java.util.List;

public interface FileShareService {
    List<SharedWithMeResponse> getSharedWithMe(String userId);
    FileShare shareFile(ShareFileRequest request);
    FileShare updatePermission(UpdatePermissionRequest request);
    FileShare updateVisibility(UpdateVisibilityRequest request);
}
