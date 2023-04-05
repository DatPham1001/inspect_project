package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the APP_CONTRACT database table.
 */
@Entity
@Table(name = "APP_CONTRACT")
@Getter
@Setter
public class AppContractReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_APP_CONTRACT", sequenceName = "SEQ_APP_CONTRACT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_APP_CONTRACT")
    private long id;

    private String address;

    @Column(name = "APP_USER_ID")
    private Long appUserId;

    @Column(name = "APPROVED_BY")
    private Long approvedBy;

    @Column(name = "APPROVED_DATE")
    private Timestamp approvedDate;

    @Column(name = "AUTHORITY_NUMBER")
    private String authorityNumber;

    @Column(name = "BANK_ACCOUNT_ID")
    private Long bankAccountId;

    @Column(name = "CARD_BACK_URL")
    private String cardBackUrl;

    @Column(name = "CARD_FRONT_URL")
    private String cardFrontUrl;

    @Column(name = "CONTRACT_FILE_URL")
    private String contractFileUrl;

    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    private String dob;

    private String fax;

    @Column(name = "ID_CARD")
    private String idCard;

    @Column(name = "ID_CARD_DATE")
    private String idCardDate;

    @Column(name = "ID_CARD_PLACE")
    private String idCardPlace;

    private String owner;

    private String phone;

    @Column(name = "POSITION")
    private String position;

    @Column(name = "SIGNATURE_URL")
    private String signatureUrl;

    private Integer status;

    @Column(name = "TAX_CODE")
    private String taxCode;

    @Column(name = "TYPE")
    private Integer type;

    private Timestamp utimestamp;

    public AppContractReport() {
    }
}