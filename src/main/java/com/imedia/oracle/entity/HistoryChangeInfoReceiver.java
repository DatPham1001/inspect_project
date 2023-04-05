package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the HISTORY_CHANGE_INFO_RECEIVER database table.
 */
@Entity
@Table(name = "HISTORY_CHANGE_INFO_RECEIVER")
@Getter
@Setter
public class HistoryChangeInfoReceiver implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "HISTORY_CHANGE_INFO_RECEIVER_SEQ", sequenceName = "HISTORY_CHANGE_INFO_RECEIVER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HISTORY_CHANGE_INFO_RECEIVER_SEQ")
    private long id;

    @Column(name = "ADDRESS_DILIVERY_ID_NEW")
    private Long addressDiliveryIdNew;

    @Column(name = "ADDRESS_DILIVERY_ID_OLD")
    private Long addressDiliveryIdOld;

    @Column(name = "CONSIGNEE_NEW")
    private String consigneeNew;

    @Column(name = "CONSIGNEE_OLD")
    private String consigneeOld;

    @CreationTimestamp
    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    @Column(name = "PHONE_NEW")
    private String phoneNew;

    @Column(name = "PHONE_OLD")
    private String phoneOld;

    @Column(name = "WEIGHT_NEW")
    private Integer weightNew;

    @Column(name = "WEIGHT_OLD")
    private Integer weightOld;

    @Column(name = "COD_OLD")
    private BigDecimal codOld;

    @Column(name = "COD_NEW")
    private BigDecimal codNew;

    @Column(name = "CHANGE_TYPE")
    private String changeType;


}