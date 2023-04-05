package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the HISTORY_ORDER_DETAIL database table.
 */
@Entity
@Table(name = "HISTORY_ORDER_DETAIL")
@Getter
@Setter
public class HistoryOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "HISTORY_ORDER_DETAIL_SEQ", sequenceName = "HISTORY_ORDER_DETAIL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HISTORY_ORDER_DETAIL_SEQ")

    private long id;

    @Column(name = "APPROVED_BY")
    private long approvedBy;

    @UpdateTimestamp
    @Column(name = "APPROVED_DATE")
    private Timestamp approvedDate;

    @Column(name = "CREATED_BY")
    private long createdBy;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    private int status;

    @Column(name = "TOATAL_FEE_OLD")
    private BigDecimal toatalFeeOld;

    @Column(name = "TOTAL_COD_NEW")
    private BigDecimal totalCodNew;

    @Column(name = "TOTAL_COD_OLD")
    private BigDecimal totalCodOld;

    @Column(name = "TOTAL_FEE_NEW")
    private BigDecimal totalFeeNew;

    public HistoryOrderDetail() {
    }


}