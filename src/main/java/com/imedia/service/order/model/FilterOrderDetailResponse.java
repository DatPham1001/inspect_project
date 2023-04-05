package com.imedia.service.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FilterOrderDetailResponse {
    private BigDecimal svcOrderDetailCode;
    private BigDecimal svcOrderId;
    private String consignee;
    private String phone;
    private Long requiredNote;
    private String note;
    private Long status;
    private OrderStatusData statusMobile;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date utimestamp;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createAt;
    private Long isPartDelivery;
    private Long isDoorDelivery;
    private String carrierOrderId;
    private String shopOrderId;
    private Long shopAddressId;
    //Shop address
    private String shopProvinceCode;
    private BigDecimal shopDistrictCode;
    private BigDecimal shopWardCode;
    private String shopProvince;
    private String shopDistrict;
    private String shopWard;
    private BigDecimal realityCod;
    private BigDecimal expectCod;

    private Long carrierId;
    private String carrierServiceCode;
    private Long idAccountCarrier;
    private Integer pickType;
    private Integer paymentType;
    //Shipper
    private String shipperName;
    private String shipperPhone;
    private String lastNote;
    //pack
    private String packName;
    private String packCode;
    private String action;
    private Integer servicePackId;
    private Integer servicePackSettingId;
    private Long type;
    //Stats
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private Long isRefund;
    private Long isPorter;
    private Long isFree;
    //Delivery
    private String deliveryAddress;
    private Long addressDeliveryId;
    private String deliveryProvince;
    private String deliveryProvinceCode;
    private String deliveryDistrict;
    private String deliveryDistrictCode;
    private String deliveryWard;
    private String deliveryWardCode;
    //Fee
    private BigDecimal totalDetailCod;
    private BigDecimal totalDetailFee;

    private List<FilterOrderDetailProductResponse> products;
    private List<FilterOrderResponseFee> detailFees;
}
