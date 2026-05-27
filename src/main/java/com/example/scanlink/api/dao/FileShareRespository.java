package com.example.scanlink.api.dao;

import com.example.scanlink.api.model.FileShare;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "file_shares")
public interface FileShareRespository extends MongoRepository<FileShare,String> {
    // tìm theo file id
    List<FileShare> findByFileId(String fileId);

    // tìm theo user share
    List<FileShare> findBySharedBy(String sharedBy);

    // tìm theo email người nhận
    List<FileShare> findBySharedTo(String sharedTo);

    // tìm theo token share
    Optional<FileShare> findByShareToken(String shareToken);

    // kiểm tra token tồn tại
    boolean existsByShareToken(String shareToken);

    // tìm các link còn active
    List<FileShare> findByActiveTrue();

    // tìm các link hết hạn trước thời gian hiện tại
    List<FileShare> findByExpiredAtBefore(LocalDateTime time);

    // tìm theo quyền
    List<FileShare> findByPermission(String permission);

    // tìm theo file và người được share
    List<FileShare> findByFileIdAndSharedTo(
            String fileId,
            String sharedTo
    );

    // xóa theo token
    void deleteByShareToken(String shareToken);

    // đếm số lần share của file
    long countByFileId(String fileId);
}
