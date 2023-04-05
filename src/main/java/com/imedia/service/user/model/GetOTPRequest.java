package com.imedia.service.user.model;

import com.imedia.model.BaseRequest;

public class GetOTPRequest extends BaseRequest {
    private String username;
    private String phone;
    private String deviceId;

    public GetOTPRequest() {
    }

    public GetOTPRequest(String username, String phone, String deviceId) {
        this.username = username;
        this.phone = phone;
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
