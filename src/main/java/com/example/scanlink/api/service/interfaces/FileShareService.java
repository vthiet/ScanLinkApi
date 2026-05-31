package com.example.scanlink.api.service.interfaces;

import com.example.scanlink.api.dto.SharedWithMeResponse;

import java.util.List;

public interface FileShareService {
    List<SharedWithMeResponse> getSharedWithMe(String userId);
}
