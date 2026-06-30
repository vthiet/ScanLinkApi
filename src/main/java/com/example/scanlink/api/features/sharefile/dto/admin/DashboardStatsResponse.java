package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalDocuments;
    private long totalStorageUsedBytes;
    private long activeUsers30Days;
    private long storageLimitMaxBytes;
}
