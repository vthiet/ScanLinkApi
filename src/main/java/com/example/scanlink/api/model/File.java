package com.example.scanlink.api.model;

import java.time.LocalDateTime;

public class File {
    private String fileName;
    private Float size;
    private LocalDateTime dateScan;
    private String path;

    public File() {
    }

    public File(String fileName, Float size, LocalDateTime dateScan, String path) {
        this.fileName = fileName;
        this.size = size;
        this.dateScan = dateScan;
        this.path = path;
    }
}
