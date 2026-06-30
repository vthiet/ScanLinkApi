package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChartPoint {
    private String date;
    private long count;
}
