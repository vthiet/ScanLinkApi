package com.example.scanlink.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private String status;
    private String message;
    private T result;

    public ApiResponse(String status, String message, T result){
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>("success", "Thành công", result);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("failed", message, null);
    }


}
