package com.example.medtrackerapp.model;

public class User {
    private String username;
    private String password;

    public User() {
        this.username = null;
        this.password = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
