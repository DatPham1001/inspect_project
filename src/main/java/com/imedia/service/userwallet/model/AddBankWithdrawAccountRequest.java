package com.imedia.service.userwallet.model;

public class AddBankWithdrawAccountRequest {
    private String username;
    private String accountNo;
    private String accUsername;
    private String bankCode;
    private Integer accountNoType;

    public Integer getAccountNoType() {
        return accountNoType;
    }

    public void setAccountNoType(Integer accountNoType) {
        this.accountNoType = accountNoType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccUsername() {
        return accUsername;
    }

    public void setAccUsername(String accUsername) {
        this.accUsername = accUsername;
    }
}
