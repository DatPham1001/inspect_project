package com.imedia.oracle.reportentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the V2_PRICE_SETTING database table.
 */
@Entity
@Table(name = "V2_PRICE_SETTING")
public class V2PriceSettingReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String code;

    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;

    private String districts;

    @Column(name = "IS_DEFAULT")
    private BigDecimal isDefault;

    private String name;

    private String provinces;

    private BigDecimal route;

    private BigDecimal status;

    @Column(name = "TYPE")
    private BigDecimal type;

    private Timestamp utimestamp;

    public V2PriceSettingReport() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getDistricts() {
        return this.districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public BigDecimal getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(BigDecimal isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinces() {
        return this.provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public BigDecimal getRoute() {
        return this.route;
    }

    public void setRoute(BigDecimal route) {
        this.route = route;
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

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

}