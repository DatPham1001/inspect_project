package com.imedia.service.user.model;

import com.imedia.oracle.entity.BankAccount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateContractRequest {
    private String owner;
    private String phone;
    private String position;
    private String address;
    private String dob;
    private String fax;
    private String idCard;
    private String idCardDate;
    private String idCardPlace;
    private String authorityNumber;
    private Integer bankAccountId;
    private String cardBackUrl;
    private String cardFrontUrl;
    private String contractFileUrl;
    private String signatureUrl;
    private String taxCode;
    private Integer type;
}
