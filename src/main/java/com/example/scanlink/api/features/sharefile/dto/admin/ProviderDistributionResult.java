package com.example.scanlink.api.features.sharefile.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
public class ProviderDistributionResult {
    @Field("_id")
    private String provider;

    private Long count;
}
