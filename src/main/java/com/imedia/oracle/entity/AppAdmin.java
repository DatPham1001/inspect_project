package com.imedia.oracle.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the APP_ADMINS database table.
 */
@Entity
@Table(name = "APP_ADMINS")
public class AppAdmin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "CREATED_BY")
    private BigDecimal createdBy;

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String email;

    @Column(name = "IS_DELETED")
    private BigDecimal isDeleted;

    @Column(name = "IS_SUPER_ADMIN")
    private BigDecimal isSuperAdmin;

    @Column(name = "LANDLINE_PHONE")
    private String landlinePhone;

    private String name;

    private String phone;

    private BigDecimal status;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "UPDATED_BY")
    private BigDecimal updatedBy;

    private Timestamp utimestamp;

    public AppAdmin() {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigDecimal createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(BigDecimal isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BigDecimal getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(BigDecimal isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public String getLandlinePhone() {
        return landlinePhone;
    }

    public void setLandlinePhone(String landlinePhone) {
        this.landlinePhone = landlinePhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(BigDecimal updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getUtimestamp() {
        return utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }
}