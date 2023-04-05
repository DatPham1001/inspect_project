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
 * The persistent class for the HISTORY_DETAIL_BUYING_FEE database table.
 */
@Entity
@Table(name = "HISTORY_DETAIL_BUYING_FEE")
public class HistoryDetailBuyingFee implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String code;
    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    private String name;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    @Column(name = "VALUE_NEW")
    private BigDecimal valueNew;

    @Column(name = "VALUE_OLD")
    private BigDecimal valueOld;

    public HistoryDetailBuyingFee() {
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

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOrderDetailCode() {
        return this.orderDetailCode;
    }

    public void setOrderDetailCode(BigDecimal orderDetailCode) {
        this.orderDetailCode = orderDetailCode;
    }

    public BigDecimal getValueNew() {
        return this.valueNew;
    }

    public void setValueNew(BigDecimal valueNew) {
        this.valueNew = valueNew;
    }

    public BigDecimal getValueOld() {
        return this.valueOld;
    }

    public void setValueOld(BigDecimal valueOld) {
        this.valueOld = valueOld;
    }

}