package com.example.scanlink.api.service.impl;

import com.example.scanlink.api.dto.request.RegisterRequestDto;
import com.example.scanlink.api.entity.UserEntity;
import com.example.scanlink.api.repository.IUserRepository;
import com.example.scanlink.api.service.IUserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserEntity syncFirebaseUser(String authorizationHeader, RegisterRequestDto dto) throws FirebaseAuthException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header format is invalid.");
        }

        String idToken = authorizationHeader.substring("Bearer ".length());

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        if (userRepository.existsById(uid)) {
            throw new IllegalArgumentException("User already exists.");
        }

        UserEntity userEntity = UserEntity.builder()
                .uid(uid)
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .role("USER")
                .isActive(true)
                .build();

        return userRepository.save(userEntity);
    }
}
