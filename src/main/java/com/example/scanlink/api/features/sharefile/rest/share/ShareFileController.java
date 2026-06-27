package com.example.scanlink.api.features.sharefile.rest.share;

import com.example.scanlink.api.dto.ApiResponse;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.service.interfaces.SharedLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/shares")
public class ShareFileController {
    private final SharedLinkService sharedLinkService;

    @PostMapping("/public")
    public ApiResponse<?> createPublicLink(Authentication authentication, CreatePublicRequest createPublicRequest) {
        String userId = (String) authentication.getPrincipal();
        CreatePublicResponse res = sharedLinkService.createSharePublic(userId, createPublicRequest);

        return ApiResponse.success(res);
    }

    @PostMapping("/private")
    public ApiResponse<?> getPrivateShares(Authentication authentication, SharePrivateRequest sharePrivateRequest) {
        String userId = (String) authentication.getPrincipal();
        SharePrivateResponse res = sharedLinkService.createSharePrivate(userId, sharePrivateRequest);

        return ApiResponse.success(res);
    }

}
