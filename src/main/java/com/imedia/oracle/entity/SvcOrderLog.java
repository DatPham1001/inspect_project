package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the SVC_ORDER_LOG database table.
 */
@Entity
@Table(name = "SVC_ORDER_LOG")
@Getter
@Setter
public class SvcOrderLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SVC_ORDER_LOG_SEQ", sequenceName = "SVC_ORDER_LOG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVC_ORDER_LOG_SEQ")
    private long id;

    @Column(name = "CREATED_BY")
    private BigDecimal createdBy;

    @Column(name = "CREATED_BY_TYPE")
    private Integer createdByType;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "FROM_STATUS")
    private Integer fromStatus;

    private String note;

    @Column(name = "ROLLBACK_FLAG")
    private Integer rollbackFlag;

    @Column(name = "SVC_ORDER_CODE")
    private BigDecimal svcOrderCode;

    @Column(name = "SVC_ORDER_DETAIL_CODE")
    private BigDecimal svcOrderDetailCode;

    @Column(name = "TO_STATUS")
    private Integer toStatus;


}