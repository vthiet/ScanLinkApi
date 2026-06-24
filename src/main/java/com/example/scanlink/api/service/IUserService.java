package com.example.scanlink.api.service;

import com.example.scanlink.api.entity.UserEntity;

import java.util.Optional;

public interface IUserService {

    Optional<UserEntity> findById(String uid);

    UserEntity save(UserEntity userEntity);
}
