package com.imedia.oracle.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the HISTORY_DETAIL_PRODUCT database table.
 */
@Entity
@Table(name = "HISTORY_DETAIL_PRODUCT")
public class HistoryDetailProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "COD_NEW")
    private BigDecimal codNew;

    @Column(name = "COD_OLD")
    private BigDecimal codOld;
    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "DETAIL_PRODUCT_ID")
    private BigDecimal detailProductId;

    public HistoryDetailProduct() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getCodNew() {
        return this.codNew;
    }

    public void setCodNew(BigDecimal codNew) {
        this.codNew = codNew;
    }

    public BigDecimal getCodOld() {
        return this.codOld;
    }

    public void setCodOld(BigDecimal codOld) {
        this.codOld = codOld;
    }

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getDetailProductId() {
        return this.detailProductId;
    }

    public void setDetailProductId(BigDecimal detailProductId) {
        this.detailProductId = detailProductId;
    }

}