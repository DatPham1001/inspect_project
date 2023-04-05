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
 * The persistent class for the ORDER_PARTIAL_REQUEST database table.
 */
@Entity
@Table(name = "ORDER_PARTIAL_REQUEST")
@Getter
@Setter
public class OrderPartialRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ORDER_PARTIAL_REQUEST_SEQ", sequenceName = "ORDER_PARTIAL_REQUEST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_PARTIAL_REQUEST_SEQ")
    private long id;

    @Column(name = "CONFIRM_BY")
    private Long confirmBy;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    private Timestamp createDate;

    @Column(name = "EXPECT_COD")
    private BigDecimal expectCod;

    @Column(name = "IMG_RETURN_ID")
    private String imgReturnId;

    @Column(name = "IMG_SUCCESS_ID")
    private String imgSuccessId;

    @Column(name = "IS_CONFIRMED")
    private Integer isConfirmed;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "PARTIAL_COD")
    private BigDecimal partialCod;

    @Column(name = "REQUEST_BY")
    private Long requestBy;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    private Timestamp updateDate;

    @Column(name = "REASON")
    private String reason;

    public OrderPartialRequest() {
    }
}