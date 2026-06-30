package com.example.scanlink.api.features.sharefile.rest.admin;

import com.example.scanlink.api.dto.ApiResponse;
import com.example.scanlink.api.features.sharefile.dto.PageResponse;
import com.example.scanlink.api.features.sharefile.dto.admin.*;
import com.example.scanlink.api.features.sharefile.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ApiResponse<DashboardStatsResponse> getDashboardStats(Authentication authentication) {
        DashboardStatsResponse dashboardStatsResponse = adminService.getDashboardStats((String) authentication.getPrincipal());
        return ApiResponse.success(dashboardStatsResponse);
    }

    @GetMapping("/dashboard/charts")
    public ApiResponse<DashboardChartResponse> getDashboardCharts(Authentication authentication,
            @RequestParam(defaultValue = "30") int days) {
        DashboardChartResponse dashboardChartResponse = adminService.getDashboardCharts((String) authentication.getPrincipal(),days);
        return ApiResponse.success(dashboardChartResponse);
    }

    // ---------- 5.2 User Administration ----------

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserAdminResponse>> getUsers(Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive) {
        return ApiResponse.success(adminService.getUsers((String) authentication.getPrincipal(),page, size, search, isActive));
    }


    @PutMapping("/users/{uid}/status")
    public ApiResponse<UserStatusUpdatedResponse> updateUserStatus(Authentication authentication,@PathVariable String uid, @RequestBody Boolean isActive) {
        return ApiResponse.success( adminService.updateActiveUser((String) authentication.getPrincipal(),uid,isActive));
    }

    @PutMapping("/users/{uid}/quota")
    public ApiResponse<UserQuotaUpdatedResponse> updateUserQuota(Authentication authentication,@PathVariable String uid, @RequestBody Long quota) {
        return ApiResponse.success(adminService.updateQuota( (String)authentication.getPrincipal(),uid,quota));
    }

    @GetMapping("/documents")
    public ApiResponse<PageResponse<DocumentAdminResponse>> getDocuments(Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String ownerUid) {
        return ApiResponse.success(adminService.getDocumentAdmins((String) authentication.getPrincipal(),page, size, search, ownerUid));
    }

    @DeleteMapping("/documents/{id}")
    public ApiResponse<Void> forceDeleteDocument(Authentication authentication,@PathVariable String id) {
        adminService.deleteDocumentAdmin((String) authentication.getPrincipal(),id);
        return ApiResponse.success(null);
    }

}
