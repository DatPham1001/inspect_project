package com.imedia.service.authenticate.model;

public class SignInSocialWalletRequest {
    //    {
//        "username": "0961744694",
//            "other_system_auth_user_id": "350098272850253",
//            "login_from": 1,
//            "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//            "email": "vietda@imediatech.com.vn",
//            "phone": "0961744694",
//            "account_epurse_id": 0
//    }
    private String username;
    private String other_system_auth_user_id;
    private Integer login_from;
    private String phone;
    private String client_identity_str;
    private String email;
    private Integer account_epurse_id = 0;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOther_system_auth_user_id() {
        return other_system_auth_user_id;
    }

    public void setOther_system_auth_user_id(String other_system_auth_user_id) {
        this.other_system_auth_user_id = other_system_auth_user_id;
    }

    public Integer getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Integer login_from) {
        this.login_from = login_from;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClient_identity_str() {
        return client_identity_str;
    }

    public void setClient_identity_str(String client_identity_str) {
        this.client_identity_str = client_identity_str;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Integer account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }
}
