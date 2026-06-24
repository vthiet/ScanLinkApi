package com.example.scanlink.api.dto.response;

public record ApiResponse<T>(String status, String message, T data) {}
