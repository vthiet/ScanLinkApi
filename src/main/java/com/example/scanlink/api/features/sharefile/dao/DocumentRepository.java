package com.example.scanlink.api.features.sharefile.dao;

import com.example.scanlink.api.features.sharefile.dto.DocumentResponse;
import com.example.scanlink.api.features.sharefile.model.Document;
import org.springframework.data.domain.Page;        // ← Spring Data
import org.springframework.data.domain.Pageable;    // ← Spring Data
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "DOCUMENTS")
public interface DocumentRepository extends MongoRepository<Document,String> {

    List<Document> findByOwnerUid(String ownerUid);
    List<Document> findByOwnerUidOrderByCreatedAtDesc(String ownerUid);
    List<Document> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    Optional<Document> findByIdAndOwnerUid(String id, String ownerUid);
    Page<Document> findByOwnerUidAndIsDeletedFalse(String ownerUid, Pageable pageable);
    Optional<Document> findBySharedLinksHashToken(String hashToken);
}
