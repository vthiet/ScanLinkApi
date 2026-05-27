package com.example.scanlink.api.dao;

import com.example.scanlink.api.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "files")
public interface FileRespository extends MongoRepository<File,String> {
    // tìm theo tên file
    List<File> findByFileName(String fileName);

    // tìm chứa keyword trong tên file
    List<File> findByFileNameContaining(String keyword);

    // tìm theo content type
    List<File> findByContentType(String contentType);

    // tìm theo user upload
    List<File> findByUploadedBy(String uploadedBy);

    // tìm theo status
    List<File> findByStatus(String status);

    // kiểm tra file đã tồn tại chưa
    boolean existsByFileName(String fileName);

    // xóa theo tên file
    void deleteByFileName(String fileName);

}
