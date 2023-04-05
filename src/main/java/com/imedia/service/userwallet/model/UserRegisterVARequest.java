package com.imedia.service.userwallet.model;

public class UserRegisterVARequest {
    private String username;
    private String sessionKey = "";

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
