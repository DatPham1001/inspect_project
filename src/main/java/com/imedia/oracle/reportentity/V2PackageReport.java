package com.imedia.oracle.reportentity;

import com.imedia.service.postage.model.PostageData;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the V2_PACKAGES database table.
 */
@Entity
@Table(name = "V2_PACKAGES")
@SqlResultSetMapping(
        name = "SpPostageDataMapping",
        classes = {
                @ConstructorResult(
                        targetClass = PostageData.class,
                        columns = {
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "status", type = Integer.class),
                                @ColumnResult(name = "type", type = Integer.class),
                                @ColumnResult(name = "time", type = String.class),
                        }
                )
        }
)
@SqlResultSetMapping(
        name = "KmPostageDataMapping",
        classes = {
                @ConstructorResult(
                        targetClass = PostageData.class,
                        columns = {
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "status", type = Integer.class),
                                @ColumnResult(name = "type", type = Integer.class),
                                @ColumnResult(name = "time", type = String.class),
                                @ColumnResult(name = "setting_id", type = Integer.class),
                                @ColumnResult(name = "max", type = Integer.class),
                                @ColumnResult(name = "base_type", type = Integer.class),
                        }
                )
        }
)
public class V2PackageReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    public Integer getPriceSettingType() {
        return priceSettingType;
    }

    public void setPriceSettingType(Integer priceSettingType) {
        this.priceSettingType = priceSettingType;
    }

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "DISCOUNT_GROUP_ID")
    private BigDecimal discountGroupId;
    @Id
    private BigDecimal id;

    private String name;

    private BigDecimal optional;

    private BigDecimal route;

    @Column(name = "SHIPPER_CONFIG")
    private BigDecimal shipperConfig;

    private BigDecimal status;

    @Column(name = "TIME")
    private String time;

    private Timestamp utimestamp;
    @Column(name = "PRICE_SETTING_TYPE")
    private Integer priceSettingType;

    public V2PackageReport() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getDiscountGroupId() {
        return this.discountGroupId;
    }

    public void setDiscountGroupId(BigDecimal discountGroupId) {
        this.discountGroupId = discountGroupId;
    }

    public BigDecimal getId() {
        return this.id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOptional() {
        return this.optional;
    }

    public void setOptional(BigDecimal optional) {
        this.optional = optional;
    }

    public BigDecimal getRoute() {
        return this.route;
    }

    public void setRoute(BigDecimal route) {
        this.route = route;
    }

    public BigDecimal getShipperConfig() {
        return this.shipperConfig;
    }

    public void setShipperConfig(BigDecimal shipperConfig) {
        this.shipperConfig = shipperConfig;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

}