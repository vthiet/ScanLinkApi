package com.example.scanlink.api.core.response;

public record ApiResponse<T>(String status, String message, T data) {}
