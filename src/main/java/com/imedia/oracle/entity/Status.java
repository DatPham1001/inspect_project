package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the STATUS database table.
 */
@Entity
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private BigDecimal code;

    private String description;

    private BigDecimal status;

    @Column(name = "TYPE")
    private BigDecimal type;

    public Status() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getCode() {
        return this.code;
    }

    public void setCode(BigDecimal code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public BigDecimal getType() {
        return this.type;
    }

    public void setType(BigDecimal type) {
        this.type = type;
    }

}