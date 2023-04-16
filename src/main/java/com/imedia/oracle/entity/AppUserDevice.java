package com.imedia.oracle.entity;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the APP_USER_DEVICES database table.
 */
@Entity
@Table(name = "APP_USER_DEVICES")
public class AppUserDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @SequenceGenerator(name = "ISEQ$$_73992", sequenceName = "ISEQ$$_73992", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ISEQ$$_73992")
    @Column(name = "ID", insertable = false)
    private long id;

    @Column(name = "APP_USER_ID")
    private BigDecimal appUserId;

    @Column(name = "CONFIRM")
    private String confirm;

    @Column(name = "DEVICE_TOKEN")
    private String deviceToken;

    private BigDecimal enabled;

    private BigDecimal ship;

    private BigDecimal shop;
    @UpdateTimestamp
    @Column(name = "UTIMESTAMP")
    private Timestamp utimestamp;

    public AppUserDevice() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(BigDecimal appUserId) {
        this.appUserId = appUserId;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public BigDecimal getEnabled() {
        return enabled;
    }

    public void setEnabled(BigDecimal enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getShip() {
        return ship;
    }

    public void setShip(BigDecimal ship) {
        this.ship = ship;
    }

    public BigDecimal getShop() {
        return shop;
    }

    public void setShop(BigDecimal shop) {
        this.shop = shop;
    }

    public Timestamp getUtimestamp() {
        return utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }
}