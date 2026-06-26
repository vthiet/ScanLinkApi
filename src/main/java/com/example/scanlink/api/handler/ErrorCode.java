package com.example.scanlink.api.handler;

public enum ErrorCode {
    NOT_FOUND(404, "Không tìm thấy tài liệu yêu cầu chia sẻ"),
    FORBIDDEN(403, "Không có quyền truy cập"),
    UNAUTHORIZED(401, "Chưa xác thực"),
    BAD_REQUEST(400, "Dữ liệu không hợp lệ"),
    INTERNAL_ERROR(500, "Lỗi hệ thống");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
