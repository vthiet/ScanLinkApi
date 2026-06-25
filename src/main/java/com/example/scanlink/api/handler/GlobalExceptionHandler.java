package com.example.scanlink.api.handler;

import com.example.scanlink.api.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException ex){
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse apiResponse = new ApiResponse(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
