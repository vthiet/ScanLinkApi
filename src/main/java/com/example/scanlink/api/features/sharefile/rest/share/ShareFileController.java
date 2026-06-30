package com.example.scanlink.api.features.sharefile.rest.share;

import com.example.scanlink.api.dto.ApiResponse;
import com.example.scanlink.api.features.sharefile.dto.*;
import com.example.scanlink.api.features.sharefile.service.interfaces.SharedLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/shares")
public class ShareFileController {
    private final SharedLinkService sharedLinkService;

    @PostMapping("/public")
    public ApiResponse<?> createPublicLink(Authentication authentication,@RequestBody CreatePublicRequest createPublicRequest) {
        String userId = (String) authentication.getPrincipal();
        CreatePublicResponse res = sharedLinkService.createSharePublic(userId, createPublicRequest);
        return ApiResponse.success(res);
    }

    @PostMapping("/public/{hashtoken}")
    public ApiResponse<?> accessPublic(@PathVariable String hashtoken, @RequestParam String password) {
        DocumentResponse res = sharedLinkService.accessPublicLink(hashtoken, password);
        return ApiResponse.success(res);
    }

    @PostMapping("/private")
    public ApiResponse<?> createPrivateLink(Authentication authentication, @RequestBody SharePrivateRequest sharePrivateRequest) {
        String userId = (String) authentication.getPrincipal();
        SharePrivateResponse res = sharedLinkService.createSharePrivate(userId, sharePrivateRequest);
        return ApiResponse.success(res);
    }

}
