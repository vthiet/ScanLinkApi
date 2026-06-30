package com.example.scanlink.api.features.authentication.service.impl;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import com.example.scanlink.api.features.authentication.repository.IUserRepository;
import com.example.scanlink.api.features.authentication.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findById(String uid) {
        return userRepository.findById(uid);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
