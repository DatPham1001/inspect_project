package com.imedia.oracle.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the APP_AUTH database table.
 */
@Entity
@Table(name = "APP_AUTH")
public class AppAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "DEVICE_TOKEN")
    private String deviceToken;

    @Lob
    private String jwt;

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "SESSION_KEY")
    private String sessionKey;

    private BigDecimal status;

    @Column(name = "USER_NAME")
    private String userName;
    @CreationTimestamp
    @Column(name = "UTIMESTAMP", updatable = false)
    private Timestamp utimestamp;

    public AppAuth() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getUtimestamp() {
        return utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }
}