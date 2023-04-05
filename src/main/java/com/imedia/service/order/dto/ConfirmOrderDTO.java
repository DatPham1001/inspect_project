package com.imedia.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrderDTO {
    private Long id;
    private BigDecimal svcOrderDetailCode;
    private Long shopId;
    private BigDecimal svcOrderId;
    private Long addressDeliveryId;
    private Long servicePackId;
    private Long servicePackSettingId;
    private String consignee;
    private String phone;
    private Long weight;
    private Long length;
    private Long width;
    private Long height;
    private Date expectPickDate;
    private Date expectDeliverDate;
    private Long isPartDelivery;
    private Long isRefund;
    private Long isPorter;
    private Long isDoorDelivery;
    private Long isDeclareProduct;
    private Long requiredNote;
    private String note;
    private Long isFree;
    private Long status;
    private Date utimestamp;
    private Date createAt;
    private Long oldStatus;
    private String carrierOrderId;
    private String shopOrderId;
    private Long shopAddressId;
    private Long carrierId;
    private String carrierServiceCode;
    private Long idAccountCarrier;
    private Integer pickType;
    private Integer paymentType;
    private BigDecimal realityCod;
    private BigDecimal expectCod;
    //order
    private Long type;
    private Long orderStatus;
    //product
    private String productName;
    private BigDecimal productValue;
    private BigDecimal productCod;
    private Integer productQuantity;
    //pack
    private String packName;
    private String packCode;
    //Shop address
    private String shopAddress;
    private String shopName;
    private String shopPhone;
    private String shopProvinceCode;
    private BigDecimal shopDistrictCode;
    private BigDecimal shopWardCode;
    //delivery address
    private String deliveryAddress;
    private String deliveryProvince;
    private String deliveryDistrict;
    private String deliveryWard;
    private String deliveryProvinceCode;
    private BigDecimal deliveryDistrictCode;
    private BigDecimal deliveryWardCode;
}
