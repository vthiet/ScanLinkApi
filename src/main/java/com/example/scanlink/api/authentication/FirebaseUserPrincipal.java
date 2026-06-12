package com.example.scanlink.api.authentication;

public record FirebaseUserPrincipal(
        String uid,
        String email,
        String name,
        boolean emailVerified
) {
}
