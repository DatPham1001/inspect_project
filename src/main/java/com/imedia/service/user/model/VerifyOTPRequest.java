package com.imedia.service.user.model;

import com.imedia.model.BaseRequest;

public class VerifyOTPRequest extends BaseRequest {
    //    {"username":"0908310776","otp_code":"[otp number]"}
    private String username;
    private String otpCode;
    private String deviceId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
