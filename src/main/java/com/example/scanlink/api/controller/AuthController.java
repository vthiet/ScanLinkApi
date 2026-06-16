package com.example.scanlink.api.controller;

import com.example.scanlink.api.dto.request.RegisterRequestDto;
import com.example.scanlink.api.dto.response.RegisterResponseDto;
import com.example.scanlink.api.entity.UserEntity;
import com.example.scanlink.api.service.IUserService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IUserService userService;

    @Autowired
    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register-sync")
    public ResponseEntity<?> syncUser(
            @RequestHeader("Authorization") String token,
            @RequestBody RegisterRequestDto requestDto) {
        try {
            UserEntity synchronizedUser = userService.syncFirebaseUser(token, requestDto);
            RegisterResponseDto responseDto = new RegisterResponseDto(synchronizedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
