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
public class FilterOrderDTO {

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
    private String carrierShortCode;

    private String feeName;
    private String feeCode;
    private BigDecimal feeValue;
    private Long type;
    private Long totalAddressDelivery;
    private Long totalDistance;
    private Long totalOrderDetail;
    private Long expectShipId;
    private Long pickupType;
    private Long orderStatus;
    private String packName;
    private String packCode;

    private String shopAddress;
    private String shopName;
    private String shopPhone;
    private String shopProvinceCode;
    private BigDecimal shopDistrictCode;
    private BigDecimal shopWardCode;

    private String deliveryAddress;
    private String deliveryProvince;
    private String deliveryDistrict;
    private String deliveryWard;
    private String deliveryProvinceCode;
    private BigDecimal deliveryDistrictCode;
    private BigDecimal deliveryWardCode;

    private String shipperPhone;
    private String shipperName;

    public FilterOrderDTO(Long id, BigDecimal svcOrderDetailCode, Long shopId, BigDecimal svcOrderId, Long addressDeliveryId, Long servicePackId, Long servicePackSettingId, String consignee, String phone, Long weight, Long length, Long width, Long height, Date expectPickDate, Date expectDeliverDate, Long isPartDelivery, Long isRefund, Long isPorter, Long isDoorDelivery, Long isDeclareProduct, Long requiredNote, String note, Long isFree, Long status, Date utimestamp, Date createAt, Long oldStatus, String carrierOrderId, String shopOrderId, Long shopAddressId, Long carrierId, String carrierServiceCode, Long idAccountCarrier, Integer pickType, Integer paymentType, BigDecimal realityCod, BigDecimal expectCod, String feeName, String feeCode, BigDecimal feeValue, Long type, Long totalAddressDelivery, Long totalDistance, Long totalOrderDetail, Long expectShipId, Long pickupType, Long orderStatus, String packName, String packCode, String shopAddress, String shopName, String shopPhone, String shopProvinceCode, BigDecimal shopDistrictCode, BigDecimal shopWardCode, String deliveryAddress, String deliveryProvince, String deliveryDistrict, String deliveryWard, String deliveryProvinceCode, BigDecimal deliveryDistrictCode, BigDecimal deliveryWardCode, String shipperPhone, String shipperName) {
        this.id = id;
        this.svcOrderDetailCode = svcOrderDetailCode;
        this.shopId = shopId;
        this.svcOrderId = svcOrderId;
        this.addressDeliveryId = addressDeliveryId;
        this.servicePackId = servicePackId;
        this.servicePackSettingId = servicePackSettingId;
        this.consignee = consignee;
        this.phone = phone;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.expectPickDate = expectPickDate;
        this.expectDeliverDate = expectDeliverDate;
        this.isPartDelivery = isPartDelivery;
        this.isRefund = isRefund;
        this.isPorter = isPorter;
        this.isDoorDelivery = isDoorDelivery;
        this.isDeclareProduct = isDeclareProduct;
        this.requiredNote = requiredNote;
        this.note = note;
        this.isFree = isFree;
        this.status = status;
        this.utimestamp = utimestamp;
        this.createAt = createAt;
        this.oldStatus = oldStatus;
        this.carrierOrderId = carrierOrderId;
        this.shopOrderId = shopOrderId;
        this.shopAddressId = shopAddressId;
        this.carrierId = carrierId;
        this.carrierServiceCode = carrierServiceCode;
        this.idAccountCarrier = idAccountCarrier;
        this.pickType = pickType;
        this.paymentType = paymentType;
        this.realityCod = realityCod;
        this.expectCod = expectCod;
        this.feeName = feeName;
        this.feeCode = feeCode;
        this.feeValue = feeValue;
        this.type = type;
        this.totalAddressDelivery = totalAddressDelivery;
        this.totalDistance = totalDistance;
        this.totalOrderDetail = totalOrderDetail;
        this.expectShipId = expectShipId;
        this.pickupType = pickupType;
        this.orderStatus = orderStatus;
        this.packName = packName;
        this.packCode = packCode;
        this.shopAddress = shopAddress;
        this.shopName = shopName;
        this.shopPhone = shopPhone;
        this.shopProvinceCode = shopProvinceCode;
        this.shopDistrictCode = shopDistrictCode;
        this.shopWardCode = shopWardCode;
        this.deliveryAddress = deliveryAddress;
        this.deliveryProvince = deliveryProvince;
        this.deliveryDistrict = deliveryDistrict;
        this.deliveryWard = deliveryWard;
        this.deliveryProvinceCode = deliveryProvinceCode;
        this.deliveryDistrictCode = deliveryDistrictCode;
        this.deliveryWardCode = deliveryWardCode;
        this.shipperPhone = shipperPhone;
        this.shipperName = shipperName;
    }

    public FilterOrderDTO(BigDecimal svcOrderDetailCode, String consignee, String phone,
                          Long weight, Long length, Long height, Long width,
                          String carrierOrderId, String shopOrderId, Long isFree, Long requiredNote,
                          BigDecimal realityCod, String note, Long status,
                          String shopName, String shopPhone, String shopAddress,
                          BigDecimal shopWardCode, String deliveryAddress,
                          String deliveryWard, Long carrierId, String carrierShortCode) {
        this.svcOrderDetailCode = svcOrderDetailCode;
        this.consignee = consignee;
        this.phone = phone;
        this.weight = weight;
        this.length = length;
        this.height = height;
        this.width = width;
        this.carrierOrderId = carrierOrderId;
        this.shopOrderId = shopOrderId;
        this.isFree = isFree;
        this.requiredNote = requiredNote;
        this.realityCod = realityCod;
        this.note = note;
        this.status = status;
        this.shopName = shopName;
        this.shopPhone = shopPhone;
        this.shopAddress = shopAddress;
        this.shopWardCode = shopWardCode;
        this.deliveryAddress = deliveryAddress;
        this.deliveryWard = deliveryWard;
        this.carrierId = carrierId;
        this.carrierShortCode = carrierShortCode;

    }

}
