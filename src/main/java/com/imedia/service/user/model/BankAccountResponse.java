package com.imedia.service.user.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BankAccountResponse {
    private BigDecimal id;
    private String bankAccount;
    private String bankAccountName;
    private String bankAddress;
    private String bankShortName;
    private String email;
    private String phone;
    private Integer type;
    private Integer withdrawType;
    private String bankCode;
    private String imageBank;
    private String bankName;
}
