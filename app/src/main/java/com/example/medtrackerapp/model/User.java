package com.example.medtrackerapp.model;

public class User {
    private String email;
    private String password;
    private String providerEmail;
    private String dateOfBirth;
    private String name;
    private String gender;

    public User() {
        this.email = null;
        this.password = null;
        this.providerEmail = null;
        this.dateOfBirth = null;
        this.name = null;
        this.gender = null;
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

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
}
