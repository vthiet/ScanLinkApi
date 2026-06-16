package com.example.scanlink.api.service;

import com.example.scanlink.api.dto.request.RegisterRequestDto;
import com.example.scanlink.api.entity.UserEntity;
import com.google.firebase.auth.FirebaseAuthException;

public interface IUserService {

    public UserEntity syncFirebaseUser(String authorizationHeader, RegisterRequestDto dto) throws FirebaseAuthException;

}
