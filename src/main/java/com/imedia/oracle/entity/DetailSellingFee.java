package com.imedia.oracle.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the DETAIL_SELLING_FEE database table.
 */
@Entity
@Table(name = "DETAIL_SELLING_FEE")
public class DetailSellingFee implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_SELLING_FEE_SEQ")
    @SequenceGenerator(name = "DETAIL_SELLING_FEE_SEQ", sequenceName = "DETAIL_SELLING_FEE_SEQ", allocationSize = 1)
    private long id;

    private String code;
    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    private String name;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;
    @UpdateTimestamp
    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "VALUE")
    private BigDecimal value;

    @Column(name = "VALUE_OLD")
    private BigDecimal valueOld;

    public DetailSellingFee() {
    }

    public DetailSellingFee(String code, String name, BigDecimal orderDetailCode, BigDecimal value) {
        this.code = code;
        this.name = name;
        this.orderDetailCode = orderDetailCode;
        this.value = value;
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

    public Timestamp getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValueOld() {
        return this.valueOld;
    }

    public void setValueOld(BigDecimal valueOld) {
        this.valueOld = valueOld;
    }

}