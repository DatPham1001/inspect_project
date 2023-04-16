package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the V2_PACKAGE_PRICE database table.
 */
@Embeddable
@Table(name = "V2_PACKAGE_PRICE")
public class V2PackagePrice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "PACKAGE_ID")
    private BigDecimal packageId;

    @Column(name = "PRICE_SETTING_ID")
    private BigDecimal priceSettingId;

    public V2PackagePrice() {
    }

    public BigDecimal getPackageId() {
        return this.packageId;
    }

    public void setPackageId(BigDecimal packageId) {
        this.packageId = packageId;
    }

    public BigDecimal getPriceSettingId() {
        return this.priceSettingId;
    }

    public void setPriceSettingId(BigDecimal priceSettingId) {
        this.priceSettingId = priceSettingId;
    }

}