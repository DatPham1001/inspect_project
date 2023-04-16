package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * The persistent class for the DISTRICTS database table.
 */
@Entity
@Table(name = "DISTRICTS")
public class District {

    @Id
    private long id;

    @Column(name = "CARRIER_CODE")
    private String carrierCode;

    private String code;

    @Column(name = "IS_INNER")
    private BigDecimal isInner;

    private String name;

    @Column(name = "PROVINCE_CODE")
    private String provinceCode;

    private String shortcode;

    private BigDecimal status;

    public District() {
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

    public BigDecimal getIsInner() {
        return this.isInner;
    }

    public void setIsInner(BigDecimal isInner) {
        this.isInner = isInner;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceCode() {
        return this.provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
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