package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the BANK_BRANCHS database table.
 */
@Entity
@Table(name = "BANK_BRANCHS")
public class BankBranch implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "BANK_ID")
    private BigDecimal bankId;

    private String code;

    private String name;

    private BigDecimal status;

    public BankBranch() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBankId() {
        return this.bankId;
    }

    public void setBankId(BigDecimal bankId) {
        this.bankId = bankId;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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