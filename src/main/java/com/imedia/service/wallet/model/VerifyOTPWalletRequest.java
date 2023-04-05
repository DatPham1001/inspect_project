package com.imedia.service.wallet.model;

public class VerifyOTPWalletRequest {
    private String username;
    private String otp_code;
    private final String phone;

    public VerifyOTPWalletRequest(String username, String otp_code, String phone) {
        this.username = username;
        this.otp_code = otp_code;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp_code() {
        return otp_code;
    }

    public void setOtp_code(String otp_code) {
        this.otp_code = otp_code;
    }
}
