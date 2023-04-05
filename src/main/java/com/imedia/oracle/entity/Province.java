package com.imedia.oracle.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the PROVINCES database table.
 */
@Entity
@Table(name = "PROVINCES")
@NamedQuery(name = "Province.findAll", query = "SELECT p FROM Province p")
public class Province implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "CARRIER_CODE")
    private String carrierCode;

    private String code;

    private String name;

    private BigDecimal rank;

    private String shortcode;

    private BigDecimal status;

    public Province() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCarrierCode() {
        return this.carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
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

    public BigDecimal getRank() {
        return this.rank;
    }

    public void setRank(BigDecimal rank) {
        this.rank = rank;
    }

    public String getShortcode() {
        return this.shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

}