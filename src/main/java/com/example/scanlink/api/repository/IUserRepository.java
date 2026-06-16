package com.example.scanlink.api.repository;

import com.example.scanlink.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByUid(String uid);

}
