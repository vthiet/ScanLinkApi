package com.example.scanlink.api.features.sharefile.service.interfaces;

import com.example.scanlink.api.features.sharefile.dto.PageResponse;
import com.example.scanlink.api.features.sharefile.dto.admin.*;
import org.springframework.data.domain.Page;

public interface AdminService {
    DashboardStatsResponse getDashboardStats(String userId);
    DashboardChartResponse getDashboardCharts(String userId,Integer days);
    PageResponse<UserAdminResponse> getUsers(String userId,int page, int size, String search, Boolean isActive);

    UserStatusUpdatedResponse updateActiveUser(String userId,String uid, Boolean isActive);
    UserQuotaUpdatedResponse updateQuota(String userId,String uid, Long quota);

    PageResponse<DocumentAdminResponse> getDocumentAdmins(String userId,int page, int size, String search, String ownerUid);
    void deleteDocumentAdmin(String userId,String id);
}
