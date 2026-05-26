package com.example.scanlink.api.dao;

import com.example.scanlink.api.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "files")
public interface FileRespository extends MongoRepository<File,String> {
}
