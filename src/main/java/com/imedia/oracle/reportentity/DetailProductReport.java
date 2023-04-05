package com.imedia.oracle.reportentity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the DETAIL_PRODUCT database table.
 */
@Entity
@Table(name = "DETAIL_PRODUCT")
public class DetailProductReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_PRODUCT_SEQ")
    @SequenceGenerator(name = "DETAIL_PRODUCT_SEQ", sequenceName = "DETAIL_PRODUCT_SEQ", allocationSize = 1)
    private long id;

    private BigDecimal cod;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    private String name;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    @Column(name = "PRODUCT_CATE_ID")
    private BigDecimal productCateId;

    private BigDecimal quantity;
    @UpdateTimestamp
    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "VALUE")
    private BigDecimal value;

    public DetailProductReport() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getCod() {
        return this.cod;
    }

    public void setCod(BigDecimal cod) {
        this.cod = cod;
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

    public BigDecimal getProductCateId() {
        return this.productCateId;
    }

    public void setProductCateId(BigDecimal productCateId) {
        this.productCateId = productCateId;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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

}