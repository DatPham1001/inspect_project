package com.imedia.service.wallet.model;

public class UpdateUserInfoWalletRequest {
    //    {
//        "username": "0908310776",
//            "password": "e10adc3949ba59abbe56e057f20f883e",
//            "login_from": 0,
//            "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//            "session_key": "[login success session]",
//            "account_epurse_id": 0,
//            "birthday":"yyyyMMdd",
//            "id_full_name":"[Ten ghi trong cmt]",
//            "id_number":"[số cmt]",
//            "display_name":"[display_name]"
//    }
    private String username;
    private String password = "";
    private String client_identity_str = "";
    private String session_key = "";
    private String birthday;
    private String id_full_name;
    private String id_number;
    private String display_name;
    private Integer login_from = 0;
    private Integer account_epurse_id = 0;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId_full_name() {
        return id_full_name;
    }

    public void setId_full_name(String id_full_name) {
        this.id_full_name = id_full_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
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
