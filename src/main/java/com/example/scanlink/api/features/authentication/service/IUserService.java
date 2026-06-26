package com.example.scanlink.api.features.authentication.service;

import com.example.scanlink.api.features.authentication.entity.UserEntity;

import java.util.Optional;

public interface IUserService {

    Optional<UserEntity> findById(String uid);

    UserEntity save(UserEntity userEntity);
}
