package com.example.scanlink.api.dao;

import com.example.scanlink.api.dto.FileHistoryResponse;
import com.example.scanlink.api.model.FileCommon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "files")
public interface FileRespository extends MongoRepository<FileCommon,String> {

    List<FileCommon> findByUserId(String userId);
    List<FileHistoryResponse> findByUserIdOrderByCreatedAtDesc(String userId);
}
