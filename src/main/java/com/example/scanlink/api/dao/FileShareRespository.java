package com.example.scanlink.api.dao;

import com.example.scanlink.api.model.FileShare;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "file_shares")
public interface FileShareRespository extends MongoRepository<FileShare,String> {
    List<FileShare> findBySharedWithUserIdOrderByShareAtDesc(String sharedWithUserId);

}
