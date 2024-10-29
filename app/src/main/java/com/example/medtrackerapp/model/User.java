package com.example.medtrackerapp.model;

public class User {
    private String email;
    private String password;
    private String providerEmail;

    public User() {
        this.email = null;
        this.password = null;
        this.providerEmail = null;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
        }

    public String getProviderEmail() {
        return providerEmail;
    }

}
