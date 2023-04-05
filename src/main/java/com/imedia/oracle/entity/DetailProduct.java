package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class DetailProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_PRODUCT_SEQ")
    @SequenceGenerator(name = "DETAIL_PRODUCT_SEQ", sequenceName = "DETAIL_PRODUCT_SEQ", allocationSize = 1)
    private long id;

    private BigDecimal cod;

    @Column(name = "CHANGED_COD")
    private BigDecimal changedCod = BigDecimal.ZERO;

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

    @Column(name = "CONFIRMED")
    private Integer confirmed = 1;


    public DetailProduct() {
    }


}