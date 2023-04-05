package com.imedia.service.wallet.model;

public class WalletUserInfoRequest {
    //    {
//        "username": "0908310776",
//            "login_from": 0,
//            "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//            "session_key": "[login success session]",
//            "account_epurse_id": 0
//    }
    private String username;
    private Integer login_from = 0;
    private String client_identity_str = "";
    private String session_key;
    private Integer account_epurse_id = 0;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Integer login_from) {
        this.login_from = login_from;
    }

    public String getClient_identity_str() {
        return client_identity_str;
    }

    public void setClient_identity_str(String client_identity_str) {
        this.client_identity_str = client_identity_str;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public Integer getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Integer account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }
}
