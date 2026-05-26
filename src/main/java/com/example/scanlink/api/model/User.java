package com.example.scanlink.api.model;

public class User {
    private Long id;
    private String username;
    private String password;
    private String identify;
    private String email;


    public User() {
    }

    public User(String username, String password, String identify, String email) {
        this.username = username;
        this.password = password;
        this.identify = identify;
        this.email = email;
    }

}
