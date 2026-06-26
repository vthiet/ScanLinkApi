package com.example.scanlink.api.features.sharefile.dao;

import com.example.scanlink.api.features.sharefile.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "files")
public interface DocumentRepository extends MongoRepository<Document,String> {

    List<Document> findByOwnerUid(String ownerUid);
    List<Document> findByOwnerUidOrderByCreatedAtDesc(String ownerUid);
    List<Document> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    Document findByIdAndOwnerUid(String id, String ownerUid);

}
