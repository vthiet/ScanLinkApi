package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.*;

@Getter
@Setter
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
