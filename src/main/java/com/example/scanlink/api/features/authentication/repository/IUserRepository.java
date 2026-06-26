package com.example.scanlink.api.features.authentication.repository;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends MongoRepository<UserEntity, String> {

}
