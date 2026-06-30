package com.example.scanlink.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message", "result"})
public class ApiResponse <T>{
    private String status;
    private String message;
    private T data;

    public ApiResponse(String status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", "Thành công", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("failed", message, null);
    }

    public static <T> ApiResponse<T> successDelete(T data) {
        return new ApiResponse<>("success", "Xóa tài liệu thành công", data);
    }
}
