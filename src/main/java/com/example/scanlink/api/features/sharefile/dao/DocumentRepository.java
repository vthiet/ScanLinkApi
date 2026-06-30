package com.example.scanlink.api.features.sharefile.dao;

import com.example.scanlink.api.features.sharefile.dto.DocumentResponse;
import com.example.scanlink.api.features.sharefile.dto.PageResponse;
import com.example.scanlink.api.features.sharefile.dto.admin.DocumentAdminResponse;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.StorageResult;
import org.springframework.data.domain.Page;        // ← Spring Data
import org.springframework.data.domain.Pageable;    // ← Spring Data
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.print.Doc;
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
    Optional<Document> findById(String id);
    @Aggregation(pipeline = {
            "{ '$group': { '_id': null, 'totalStorage': { '$sum': '$fileSize' } } }"
    })
    StorageResult sumStorageUsedBytes();


    // search + ownerUid
    Page<Document> findByTitleContainingIgnoreCaseAndOwnerUidAndIsDeletedFalse(String title, String ownerUid, Pageable pageable);

    // chỉ search
    Page<Document> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title, Pageable pageable);


    // không filter gì, lấy tất cả (chưa xóa)
    Page<Document> findByIsDeletedFalse(Pageable pageable);

}
