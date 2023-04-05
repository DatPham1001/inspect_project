package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PushKmOrderDetailRequest {
    private long id;

    private Long addressDeliveryId;

    private Integer carrierId;

    private String carrierOrderId;

    private String carrierServiceCode;

    private String consignee;

    private String expectDeliverDate;

    private String expectPickDate;

    private Integer height;

    private Integer idAccountCarrier;

    private Integer isDeclareProduct;

    private Integer isDoorDelivery;

    private Integer isFree;

    private Integer isPartDelivery;

    private Integer isPorter;

    private Integer isRefund;

    private Integer length;

    private String note;

    private Integer oldStatus;

    private String phone;

    private Integer pickType;

    private Integer requiredNote;

    private Integer servicePackId;

    private Integer servicePackSettingId;

    private Integer shopAddressId;

    private Long shopId;

    private String shopOrderId;

    private Integer status;

    private BigDecimal svcOrderDetailCode;

    private BigDecimal svcOrderId;


    private Integer weight;

    private Integer width;

    private Integer paymentType;
}
