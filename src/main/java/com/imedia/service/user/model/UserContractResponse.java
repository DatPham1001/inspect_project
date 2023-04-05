package com.imedia.service.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imedia.oracle.entity.BankAccount;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;
@Getter
@Setter
public class UserContractResponse {
    private String owner;
    private String companyName;
    private String phone;
    private String position;
    private String address;
    private String authorityNumber;
    private BankAccountResponse bankAccount;
    private String cardBackUrl;
    private String cardFrontUrl;
    private String contractFileUrl;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp createAt;
    private String dob;
    private String fax;
    private String idCard;
    private String idCardDate;
    private String idCardPlace;
    private String signatureUrl;
    private Integer status;
    private String taxCode;
    private Integer type;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp utimestamp;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp approvedDate;
}
