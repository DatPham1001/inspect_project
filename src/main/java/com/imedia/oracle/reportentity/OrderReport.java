package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDERS database table.
 */
@Entity
@Table(name = "ORDERS")
@Getter
@Setter
public class OrderReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    @Column(name = "DECLARE_PRODUCT_FEE")
    private BigDecimal declareProductFee;

    @Column(name = "DOOR_DELIVERY_FEE")
    private BigDecimal doorDeliveryFee;

    @Column(name = "EXPECT_SHIP_ID")
    private BigDecimal expectShipId;

    @Column(name = "IS_FINISH")
    private BigDecimal isFinish;

    private String note;

    @Column(name = "ORDER_CODE")
    private BigDecimal orderCode;

    @Column(name = "OTHER_FEE")
    private BigDecimal otherFee;

    @Column(name = "PORTER_FEE")
    private BigDecimal porterFee;

    @Column(name = "REFUND_FEE")
    private BigDecimal refundFee;

    @Column(name = "SERVICE_PACK_ID")
    private BigDecimal servicePackId;

    @Column(name = "SERVICE_PACK_SETTING_ID")
    private BigDecimal servicePackSettingId;

    @Column(name = "SHIP_BASE_FEE")
    private BigDecimal shipBaseFee;

    @Column(name = "SHIP_CHECK_POINT_FEE")
    private BigDecimal shipCheckPointFee;

    @Column(name = "SHIP_DISTANCE_FEE")
    private BigDecimal shipDistanceFee;

    @Column(name = "SHIP_ID")
    private BigDecimal shipId;

    @Column(name = "SHIP_ORDER_DETAIL_FEE")
    private BigDecimal shipOrderDetailFee;

    @Column(name = "SHOP_ADDRESS_ID")
    private BigDecimal shopAddressId;

    @Column(name = "SHOP_ID")
    private BigDecimal shopId;

    @Column(name = "SHORT_ID")
    private String shortId;

    @Column(name = "STATE")
    private BigDecimal state;

    private BigDecimal status;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "TOTAL_CHECK_POINT")
    private BigDecimal totalCheckPoint;

    @Column(name = "TOTAL_COD")
    private BigDecimal totalCod;

    @Column(name = "TOTAL_DISTANCE")
    private BigDecimal totalDistance;

    @Column(name = "TOTAL_HOLD_SHIP")
    private BigDecimal totalHoldShip;

    @Column(name = "TOTAL_HOLD_SHOP")
    private BigDecimal totalHoldShop;

    @Column(name = "TOTAL_ORDER_DETAIL")
    private BigDecimal totalOrderDetail;

    @Column(name = "TOTAL_PRODUCT_VALUE")
    private BigDecimal totalProductValue;

    @Column(name = "TOTAL_SHIP_FEE")
    private BigDecimal totalShipFee;

    @Column(name = "TYPE")
    private BigDecimal type;

    private Timestamp utimestamp;

    public OrderReport() {
    }

}