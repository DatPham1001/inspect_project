package com.imedia.service.wallet.model;

import java.math.BigDecimal;

public class WalletUserInfoResponse {
    //    {
//        "username": "0936986861",
//            "password": "*****",
//            "remain_balance": 0,
//            "holding_balance": 0,
//            "other_system_auth_user_id": "EAAH03RPyKFQBAMZBrVAGGSIO9xXZAEQqzBO3Q9h2We9NP83jyxBGqT8rWyeZA1NWSyIgvCaBsj16eK3xZAeTeBKLGFOCQIiOmc3ZB0qeEKZCbqUfKBF0BqrZBIQrvp1eQrB2WnXJOIPitzqB9KoSnRbBblhvNsLUXP8LBNubGyFSeYfsJibmgT8yZCD6JTDzDUZBUEY20cpMjmEkLjDodQmJ6XzUglzg9hYUZD",
//            "login_from": 1,
//            "email": "",
//            "phone": "0936986861",
//            "session_key": "377383356f5e4f6ca661eaf5cfb4eb55-1625382092661",
//            "balance": [],
//        "account_epurse_id": 4442
//    }
    private String username;
    private String password;
    private String other_system_auth_user_id;
    private String email;
    private String phone;
    private String session_key;
    private Long account_epurse_id;
    private Integer login_from;
    private BigDecimal remain_balance;
    private BigDecimal holding_balance;

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

    public String getOther_system_auth_user_id() {
        return other_system_auth_user_id;
    }

    public void setOther_system_auth_user_id(String other_system_auth_user_id) {
        this.other_system_auth_user_id = other_system_auth_user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public Long getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Long account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }

    public Integer getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Integer login_from) {
        this.login_from = login_from;
    }

    public BigDecimal getRemain_balance() {
        return remain_balance;
    }

    public void setRemain_balance(BigDecimal remain_balance) {
        this.remain_balance = remain_balance;
    }

    public BigDecimal getHolding_balance() {
        return holding_balance;
    }

    public void setHolding_balance(BigDecimal holding_balance) {
        this.holding_balance = holding_balance;
    }
}
