package com.imedia.service.wallet.model;

import java.math.BigDecimal;
import java.util.List;

public class WalletUserInfo {
    // "username": "0912384753",
//         "password": "*****",
//         "newpassword": "*****",
//         "remain_balance": 0,
//         "holding_balance": 0,
//         "bonus_bal": 0,
//         "require_change_pass": 0,
//         "login_from": 0,
//         "client_identity_str": "",
//         "email": "2235f453@gmail.com",
//         "phone": "0912384753",
//         "session_key": "*****",
//         "birthday": "19000101",
//         "account_epurse_id": 4571
    private String username;
    private String phone;
    private BigDecimal remain_balance;
    private BigDecimal holding_balance;
    private BigDecimal bonus_bal;
    private Long account_epurse_id;
    private String email;
    private List<WalletUserBalanceData> balance;

    public Long getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Long account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
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

    public BigDecimal getBonus_bal() {
        return bonus_bal;
    }

    public void setBonus_bal(BigDecimal bonus_bal) {
        this.bonus_bal = bonus_bal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<WalletUserBalanceData> getBalance() {
        return balance;
    }

    public void setBalance(List<WalletUserBalanceData> balance) {
        this.balance = balance;
    }
}
