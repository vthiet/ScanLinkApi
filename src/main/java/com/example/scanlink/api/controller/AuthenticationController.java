package com.example.scanlink.api.controller;

import com.example.scanlink.api.dto.response.ApiResponse;
import com.example.scanlink.api.entity.UserEntity;
import com.example.scanlink.api.service.IUserService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final IUserService userService;

    @Autowired
    public AuthenticationController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerOrSyncUser() {
        FirebaseToken token =
                (FirebaseToken) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials();

        UserEntity userEntity = userService.findById(token.getUid()).orElseGet(() -> {
            UserEntity newUser = UserEntity.builder()
                    .uid(token.getUid())
                    .email(token.getEmail())
                    .displayName(token.getName())
                    .photoUrl(token.getPicture())
                    .role("USER")
                    .isActive(true)
                    .build();

            return userService.save(newUser);
        });

        return ResponseEntity.ok(new ApiResponse<>("success", "Sync thành công", userEntity));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        FirebaseToken token =
                (FirebaseToken) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getCredentials();

        UserEntity userEntity = userService.findById(token.getUid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tài khoản không tồn tại"));

        return ResponseEntity.ok(new ApiResponse<>("success", "Đăng nhập thành công", userEntity));
    }

}
