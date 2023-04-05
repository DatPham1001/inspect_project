package com.imedia.service.wallet.model;

public class GetOTPWalletRequest {
    //        "username": "0961744694",
//                "other_system_auth_user_id": "350098272850253",
//                "login_from": 1,
//                "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//                "phone": "0961744698",
//                "account_epurse_id": 0
    private String username;
    private String other_system_auth_user_id;
    private String client_identity_str;
    private String phone;
    private Integer login_from = 1;
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

    public String getClient_identity_str() {
        return client_identity_str;
    }

    public void setClient_identity_str(String client_identity_str) {
        this.client_identity_str = client_identity_str;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Integer login_from) {
        this.login_from = login_from;
    }

    public Integer getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Integer account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }
}
