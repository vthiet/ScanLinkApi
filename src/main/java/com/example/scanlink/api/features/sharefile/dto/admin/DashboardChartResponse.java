package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardChartResponse {
    private List<ChartPoint> registrationChart;
    private List<ChartPoint> uploadChart;
    private Map<String, Long> providerDistribution;

}
