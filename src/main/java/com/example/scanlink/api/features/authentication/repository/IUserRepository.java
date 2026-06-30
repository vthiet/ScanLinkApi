package com.example.scanlink.api.features.authentication.repository;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IUserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
    long countByLastLoginAtAfter(LocalDateTime dateTime);
    UserEntity findByUid(String uid);
    boolean existsByUidAndRole(String uid, String role);
}
