package com.imedia.service.user.model;

import com.imedia.model.BaseRequest;

public class UpdatePhoneRequest extends BaseRequest {
    private String username;
    private String phoneOTP;
    private String newPhone;

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneOTP() {
        return phoneOTP;
    }

    public void setPhoneOTP(String phoneOTP) {
        this.phoneOTP = phoneOTP;
    }
}
