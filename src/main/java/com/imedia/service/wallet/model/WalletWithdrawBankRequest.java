package com.imedia.service.wallet.model;

import java.math.BigDecimal;

public class WalletWithdrawBankRequest {
    //    {
//        "client_request_id": "WITHDRAWAL_1626056592546",
//            "session_key": "xxx",
//            "bal_stack_code": "73017ed33b64273d17b85efaa30e4492",
//            "username": "0933045719",
//            "otp_code": "777915",
//            "balChangeAmount": 1000,
//            "createdBy": "0933045719",
//            "bankCode": "970410",
//            "accBankName": "MASTER SHIP",
//            "accBankCode": "97041812343435",
//            "bankAccType": 0
//    }
    private String client_request_id;
    private String session_key;
    private String bal_stack_code;
    private String username;
    private String otp_code;
    private BigDecimal balChangeAmount;
    private BigDecimal transaction_fee;
    private String createdBy;
    private String bankCode;
    private String accBankName;
    private String accBankCode;
    private Long bankAccType;
    private boolean auto_withdrawal;
    private String balance_sub_type;

    public BigDecimal getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(BigDecimal transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getBalance_sub_type() {
        return balance_sub_type;
    }

    public void setBalance_sub_type(String balance_sub_type) {
        this.balance_sub_type = balance_sub_type;
    }

    public boolean getAuto_withdrawal() {
        return auto_withdrawal;
    }

    public void setAuto_withdrawal(boolean auto_withdrawal) {
        this.auto_withdrawal = auto_withdrawal;
    }

    public String getClient_request_id() {
        return client_request_id;
    }

    public void setClient_request_id(String client_request_id) {
        this.client_request_id = client_request_id;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getBal_stack_code() {
        return bal_stack_code;
    }

    public void setBal_stack_code(String bal_stack_code) {
        this.bal_stack_code = bal_stack_code;
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

    public BigDecimal getBalChangeAmount() {
        return balChangeAmount;
    }

    public boolean isAuto_withdrawal() {
        return auto_withdrawal;
    }

    public void setBalChangeAmount(BigDecimal balChangeAmount) {
        this.balChangeAmount = balChangeAmount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccBankName() {
        return accBankName;
    }

    public void setAccBankName(String accBankName) {
        this.accBankName = accBankName;
    }

    public String getAccBankCode() {
        return accBankCode;
    }

    public void setAccBankCode(String accBankCode) {
        this.accBankCode = accBankCode;
    }

    public Long getBankAccType() {
        return bankAccType;
    }

    public void setBankAccType(Long bankAccType) {
        this.bankAccType = bankAccType;
    }
}
