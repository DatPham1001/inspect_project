package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the SVC_ORDERS database table.
 */
@Entity
@Table(name = "SVC_ORDERS")
@Getter
@Setter
public class SvcOrderReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SVC_ORDERS_SEQ", sequenceName = "SVC_ORDERS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVC_ORDERS_SEQ")
    private Long id;
    @CreationTimestamp
    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    @Column(name = "EXPECT_SHIP_ID")
    private Long expectShipId;

    @Column(name = "ORDER_CODE")
    private BigDecimal orderCode;

    @Column(name = "SERVICE_PACK_ID")
    private BigDecimal servicePackId;

    @Column(name = "SERVICE_PACK_SETTING_ID")
    private BigDecimal servicePackSettingId;

    @Column(name = "SHOP_ADDRESS_ID")
    private Integer shopAddressId;

    @Column(name = "SHOP_ID")
    private BigDecimal shopId;

    private Integer status;

    @Column(name = "TOTAL_ADDRESS_DILIVERY")
    private Long totalAddressDilivery;

    @Column(name = "TOTAL_DISTANCE")
    private Long totalDistance;

    @Column(name = "TOTAL_ORDER_DETAIL")
    private Integer totalOrderDetail;

    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "SHOP_ORDER_ID")
    private String shopOrderId;
    @Column(name = "PICKUP_TYPE")
    private Integer pickupType;
    @UpdateTimestamp
    private Timestamp utimestamp;
    @Column(name = "SHOP_ID_REFERENCE")
    private Long shopIdReference;
    @Column(name = "ID_DELETED")
    private Integer isDeleted;
    public SvcOrderReport() {
    }


}