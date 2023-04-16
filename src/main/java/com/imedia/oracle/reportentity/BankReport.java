package com.imedia.oracle.reportentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the BANKS database table.
 */
@Entity
@Table(name = "BANKS")
public class BankReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "BANK_CODE")
    private String bankCode;

    private String code;

    @Column(name = "IMAGE_BANK")
    private String imageBank;

    @Column(name = "NAME")
    private String name;

    private BigDecimal status;

    public BankReport() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageBank() {
        return this.imageBank;
    }

    public void setImageBank(String imageBank) {
        this.imageBank = imageBank;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

}