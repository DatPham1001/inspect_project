package com.imedia.oracle.reportentity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the DETAIL_BUYING_FEE database table.
 */
@Entity
@Table(name = "DETAIL_BUYING_FEE")
public class DetailBuyingFeeReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_BUYING_FEE_SEQ")
    @SequenceGenerator(name = "DETAIL_BUYING_FEE_SEQ", sequenceName = "DETAIL_BUYING_FEE_SEQ", allocationSize = 1)
    private long id;

    private String code;
    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    private String name;

    @Column(name = "ORDER_DETAIL_ID")
    private BigDecimal orderDetailId;
    @UpdateTimestamp
    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "VALUE")
    private BigDecimal value;

    @Column(name = "VALUE_OLD")
    private BigDecimal valueOld;

    public DetailBuyingFeeReport() {
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

    public BigDecimal getOrderDetailId() {
        return this.orderDetailId;
    }

    public void setOrderDetailId(BigDecimal orderDetailId) {
        this.orderDetailId = orderDetailId;
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