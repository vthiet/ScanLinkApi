package com.example.scanlink.api.features.sharefile.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class ChartAggregationResult {

    @Field("_id")
    private String date;

    private Long count;
}
