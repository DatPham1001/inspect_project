package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the APP_USER_LOCATIONS database table.
 */
@Entity
@Table(name = "APP_USER_LOCATIONS")
public class AppUserLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "APP_USER_ID")
    private long appUserId;

    private String description;

    @Column(name = "DEVICE_TOKEN")
    private String deviceToken;

    @Column(name = "DISTRICT_ID")
    private BigDecimal districtId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Column(name = "PROVINCE_ID")
    private BigDecimal provinceId;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "UPDATE_AT")
    private Timestamp updateAt;

    @Column(name = "WARD_ID")
    private BigDecimal wardId;

    public AppUserLocation() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(long appUserId) {
        this.appUserId = appUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public BigDecimal getDistrictId() {
        return districtId;
    }

    public void setDistrictId(BigDecimal districtId) {
        this.districtId = districtId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(BigDecimal provinceId) {
        this.provinceId = provinceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }

    public BigDecimal getWardId() {
        return wardId;
    }

    public void setWardId(BigDecimal wardId) {
        this.wardId = wardId;
    }
}