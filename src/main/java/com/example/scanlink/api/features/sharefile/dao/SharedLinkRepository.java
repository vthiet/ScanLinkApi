package com.example.scanlink.api.features.sharefile.dao;

import com.example.scanlink.api.features.sharefile.model.SharedLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "file_shares")
public interface SharedLinkRepository extends MongoRepository<SharedLink,String> {
    List<SharedLink> findByShareWithUserIdOrderByShareAtDesc(String sharedWithUserId);
    SharedLink findByDocumentId(String documentId);
    SharedLink findByDocumentIdAndShareWithUserId(String documentId, String shareWithUserId);
}
