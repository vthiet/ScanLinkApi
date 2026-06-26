package com.example.scanlink.api.handler;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND("Not Found", "Không tìm thấy tài liệu yêu cầu chia sẻ"),
    FORBIDDEN("FORBIDDEN", "Không có quyền truy cập"),
    UNAUTHORIZED("UNAUTHORIZED", "Chưa xác thực"),
    BAD_REQUEST("BAD_REQUEST", "Dữ liệu không hợp lệ"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Lỗi hệ thống");

    private final String status;
    private final String message;

    ErrorCode(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
