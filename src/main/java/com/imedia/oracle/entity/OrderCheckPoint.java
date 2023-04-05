package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the ORDER_CHECK_POINTS database table.
 */
@Entity
@Table(name = "ORDER_CHECK_POINTS")
@Getter
@Setter
public class OrderCheckPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String address;

    @Column(name = "DISTANCE_TO_NEXT")
    private BigDecimal distanceToNext;

    @Column(name = "DISTANCE_TO_PICK")
    private BigDecimal distanceToPick;

    @Column(name = "DISTRICT_ID")
    private Integer districtId;

    @Column(name = "IS_INNER")
    private BigDecimal isInner;

    private Double latitude;

    private Double longitude;

    @Column(name = "ORDER_ID")
    private BigDecimal orderId;

    @Column(name = "PROVINCE_ID")
    private Integer provinceId;

    private BigDecimal rank;

    @Column(name = "REF_ID")
    private BigDecimal refId;

    private Integer status;

    @Column(name = "TYPE")
    private BigDecimal type;

    @Column(name = "WARD_ID")
    private Integer wardId;

    public OrderCheckPoint() {
    }

}