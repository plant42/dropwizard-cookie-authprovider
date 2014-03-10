package com.plant42.dropwizard.auth;


public class User {
    private final String code;

    public User(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
