package com.example.scanlink.api.handler;

public class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
}
