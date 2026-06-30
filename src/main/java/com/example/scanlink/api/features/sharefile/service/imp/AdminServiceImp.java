package com.example.scanlink.api.features.sharefile.service.imp;

import com.example.scanlink.api.features.authentication.entity.UserEntity;
import com.example.scanlink.api.features.authentication.repository.IUserRepository;
import com.example.scanlink.api.features.sharefile.dao.DocumentRepository;
import com.example.scanlink.api.features.sharefile.dto.PageResponse;
import com.example.scanlink.api.features.sharefile.dto.admin.*;
import com.example.scanlink.api.features.sharefile.model.ChartAggregationResult;
import com.example.scanlink.api.features.sharefile.model.Document;
import com.example.scanlink.api.features.sharefile.service.interfaces.AdminService;
import com.example.scanlink.api.features.sharefile.service.interfaces.StorageResult;
import com.example.scanlink.api.handler.AppException;
import com.example.scanlink.api.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.aggregation.DateOperators;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {
    private final IUserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public DashboardStatsResponse getDashboardStats(String userId) {
        checkAdmin(userId);
        long totalUsers = userRepository.count();

        long totalDocuments = documentRepository.count();

        StorageResult result = documentRepository.sumStorageUsedBytes();

        long totalStorage = result == null || result.getTotalStorage() == null ? 0 : result.getTotalStorage();

        long activeUsers = userRepository.countByLastLoginAtAfter(
                LocalDateTime.now().minusDays(30)
        );

        long storageLimit = 500L * 1024 * 1024 * 1024;
        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .storageLimitMaxBytes(storageLimit)
                .activeUsers30Days(activeUsers)
                .totalDocuments(totalDocuments)
                .totalStorageUsedBytes(totalStorage).build();
    }

    @Override
    public DashboardChartResponse getDashboardCharts(String userId,int days) {
        checkAdmin(userId);
        return DashboardChartResponse.builder()
                .registrationChart(getRegistrationChart(days))
                .uploadChart(getUploadChart(days))
                .providerDistribution(getProviderDistribution())
                .build();
    }

    @Override
    public PageResponse<UserAdminResponse> getUsers(String userId,int page, int size, String search, Boolean isActive) {
        checkAdmin(userId);
        Query query = new Query();

        List<Criteria> criteria = new ArrayList<>();

        // search email hoặc displayName
        if (search != null && !search.isBlank()) {

            criteria.add(
                    new Criteria().orOperator(
                            Criteria.where("email")
                                    .regex(Pattern.quote(search), "i"),

                            Criteria.where("displayName")
                                    .regex(Pattern.quote(search), "i")
                    )
            );

        }

        // filter active
        if (isActive != null) {
            criteria.add(
                    Criteria.where("isActive")
                            .is(isActive)
            );
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(
                    new Criteria().andOperator(
                            criteria.toArray(new Criteria[0])
                    )
            );
        }

        long total = mongoTemplate.count(query, UserEntity.class);

        query.with(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        List<UserEntity> users = mongoTemplate.find(query, UserEntity.class);
        List<UserAdminResponse> content = users.stream().map(this::mapToResponse).toList();
        Page<UserAdminResponse> pageResult = new PageImpl<>(content, PageRequest.of(page, size), total);

        return PageResponse.of(pageResult);
    }

    @Override
    public UserStatusUpdatedResponse updateActiveUser(String userId,String uid, UpdateUserStatusRequest request) {
        checkAdmin(userId);
        if (uid == null) throw new AppException(ErrorCode.NOT_FOUND);
        UserEntity user = userRepository.findByUid(uid);
        if (user == null) throw new AppException(ErrorCode.NOT_FOUND);
        user.setActive(request.getIsActive());
        userRepository.save(user);
        return new UserStatusUpdatedResponse(user.getUid(), user.isActive(), LocalDateTime.now());
    }

    @Override
    public UserQuotaUpdatedResponse updateQuota(String userId,String uid, UpdateQuotaRequest quota) {
        checkAdmin(userId);
        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() ->
                        new AppException(ErrorCode.NOT_FOUND));

        user.setStorageLimit(quota.getQuota());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserQuotaUpdatedResponse.builder()
                .uid(user.getUid())
                .storageLimit(user.getStorageLimit())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public PageResponse<DocumentAdminResponse> getDocumentAdmins(String userId, int page, int size, String search, String ownerUid) {
        checkAdmin(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasOwnerUid = ownerUid != null && !ownerUid.isBlank();

        Page<com.example.scanlink.api.features.sharefile.model.Document> documentPage;

        if (hasSearch && hasOwnerUid) {
            documentPage = documentRepository
                    .findByTitleContainingIgnoreCaseAndOwnerUidAndIsDeletedFalse(search, ownerUid, pageable);
        } else if (hasSearch) {
            documentPage = documentRepository
                    .findByTitleContainingIgnoreCaseAndIsDeletedFalse(search, pageable);
        } else if (hasOwnerUid) {
            documentPage = documentRepository
                    .findByOwnerUidAndIsDeletedFalse(ownerUid, pageable);
        } else {
            documentPage = documentRepository.findByIsDeletedFalse(pageable);
        }

        Page<DocumentAdminResponse> responsePage = documentPage.map(this::toAdminResponse);

        return PageResponse.of(responsePage);
    }

    @Override
    public void deleteDocumentAdmin(String userId, String id) {
        checkAdmin(userId);
        if(id == null) throw new AppException(ErrorCode.NOT_FOUND);
        com.example.scanlink.api.features.sharefile.model.Document document = documentRepository.findById(id)
                .orElseThrow();
        if(!document.getIsDeleted()){
            document.setIsDeleted(true);
        }
        documentRepository.save(document);
    }

    private void checkAdmin(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (!userRepository.existsByUidAndRole(userId, "ADMIN")) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
    private UserAdminResponse mapToResponse(UserEntity user) {

        return UserAdminResponse.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .photoUrl(user.getPhotoUrl())
                .role(user.getRole())
                .isActive(user.isActive())
                .storageUsed(user.getStorageUsed())
                .storageLimit(user.getStorageLimit())
                .providerId(user.getProviderId())
                .createdAt(user.getCreatedAt())
                .build();

    }
    private DocumentAdminResponse toAdminResponse(com.example.scanlink.api.features.sharefile.model.Document doc) {
        return DocumentAdminResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .ownerUid(doc.getOwnerUid())
                .fileSize(doc.getFileSize())
                .storageUrl(doc.getStorageUrl())
                .createdAt(doc.getCreatedAt())
                .build();
    }
    private List<ChartPoint> getRegistrationChart(int days) {

        LocalDateTime from = LocalDateTime.now().minusDays(days);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("createdAt").gte(from)
                ),
                Aggregation.project()
                        .and(DateOperators.DateToString.dateOf("createdAt").toString("%Y-%m-%d"))
                        .as("date"),
                Aggregation.group("date")
                        .count()
                        .as("count"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"))
        );

        AggregationResults<ChartAggregationResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        UserEntity.class,
                        ChartAggregationResult.class
                );

        return results.getMappedResults()
                .stream()
                .map(item -> new ChartPoint(
                        item.getDate(),
                        item.getCount()
                ))
                .toList();
    }

    private List<ChartPoint> getUploadChart(int days) {

        LocalDateTime from = LocalDateTime.now().minusDays(days);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("createdAt").gte(from)
                                .and("isDeleted").is(false)
                ),
                Aggregation.project()
                        .and(DateOperators.DateToString.dateOf("createdAt").toString("%Y-%m-%d"))
                        .as("date"),
                Aggregation.group("date")
                        .count()
                        .as("count"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"))
        );

        AggregationResults<ChartAggregationResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        Document.class,
                        ChartAggregationResult.class
                );

        return results.getMappedResults()
                .stream()
                .map(item -> new ChartPoint(
                        item.getDate(),
                        item.getCount()
                ))
                .toList();
    }

    private Map<String, Long> getProviderDistribution() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("provider")
                        .count()
                        .as("count")
        );

        AggregationResults<ProviderDistributionResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        UserEntity.class,
                        ProviderDistributionResult.class
                );

        return results.getMappedResults()
                .stream()
                .filter(item -> item.getProvider() != null)
                .collect(Collectors.toMap(
                        ProviderDistributionResult::getProvider,
                        ProviderDistributionResult::getCount,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

}

