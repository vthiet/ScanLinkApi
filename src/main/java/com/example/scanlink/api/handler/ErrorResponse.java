package com.example.scanlink.api.handler;


public class ErrorResponse extends RuntimeException{
    private int status;
    private String message;
    private Long timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}
