package com.imedia.service.userwallet.model;

import java.math.BigDecimal;

public class BankAccountDTO {
    private BigDecimal id;
    private String bankAccount;
    private String bankAccountName;
    private String phone;
    private BigDecimal roleType;
    private Integer status;
    private Integer type;
    private Integer withdrawType;
    private String bankCode;
    private String bankName;
    private String bankShortName;

    public BankAccountDTO(BigDecimal id, String bankAccount, String bankAccountName, String phone, BigDecimal roleType, Integer status, Integer type, Integer withdrawType, String bankCode, String bankName, String bankShortName) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.phone = phone;
        this.roleType = roleType;
        this.status = status;
        this.type = type;
        this.withdrawType = withdrawType;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankShortName = bankShortName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getRoleType() {
        return roleType;
    }

    public void setRoleType(BigDecimal roleType) {
        this.roleType = roleType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(Integer withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
