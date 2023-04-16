package com.imedia.service.authenticate.model;

public class SignInWalletResponse {
    //    {
//        "username": "0908310776",
//            "password": "*****",
//            "remain_balance": 992613900,
//            "holding_balance": 7282150,
//            "require_change_pass": 0,
//            "display_name": "Hoàng Tùng Lâm",
//            "login_from": 0,
//            "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//            "session_key": "d2903e0387b041ce98ffc72c6d3ccc56",
//            "account_epurse_id": 124
//    }
    private String username;
    private String password;
    private String display_name;
    private String client_identity_str;
    private String session_key;
    private Long remain_balance;
    private Long holding_balance;
    private Long require_change_pass;
    private Long login_from;
    private Long account_epurse_id;

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

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
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

    public Long getRemain_balance() {
        return remain_balance;
    }

    public void setRemain_balance(Long remain_balance) {
        this.remain_balance = remain_balance;
    }

    public Long getHolding_balance() {
        return holding_balance;
    }

    public void setHolding_balance(Long holding_balance) {
        this.holding_balance = holding_balance;
    }

    public Long getRequire_change_pass() {
        return require_change_pass;
    }

    public void setRequire_change_pass(Long require_change_pass) {
        this.require_change_pass = require_change_pass;
    }

    public Long getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Long login_from) {
        this.login_from = login_from;
    }

    public Long getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Long account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }
}
