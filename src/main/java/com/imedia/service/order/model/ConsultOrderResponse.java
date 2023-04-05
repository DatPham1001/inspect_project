package com.imedia.service.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConsultOrderResponse {
    private BigDecimal svcOrderDetailCode;
    private String carrierOrderId;
    //Shop address
    private String shopName;
    private String shopPhone;
//    private Long shopAddressId;
    private String shopAddress;
    private String shopProvinceCode;
    private BigDecimal shopDistrictCode;
    private BigDecimal shopWardCode;
    private String shopProvince;
    private String shopDistrict;
    private String shopWard;
    private String consignee;
    private String phone;
    private int type;

    private String deliveryAddress;
    private String deliveryProvince;
    private String deliveryDistrict;
    private String deliveryWard;
    private String deliveryProvinceCode;
    private BigDecimal deliveryDistrictCode;
    private BigDecimal deliveryWardCode;

//    private Long servicePackId;
//    private Long servicePackSettingId;
    private String packName;
    private String packCode;

    private Long weight;
    private Long length;
    private Long width;
    private Long height;

//    private int status;
    private OrderStatusData statusMobile;
//    private Long oldStatus;
    private Long isPartDelivery;
    private Long isRefund;
    private Long isPorter;
    private Long isDoorDelivery;
    private Long requiredNote;
    private String note;
    private Long isFree;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date utimestamp;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createAt;
    private BigDecimal realityCod;
    private BigDecimal totalFee;
    private List<FilterOrderResponseFee> fees;
    private List<FilterOrderDetailProductResponse> products;
    private FilterOrderShipperData shipper;
    private List<OrderTrackingResponse> trackings;

    public ConsultOrderResponse() {
    }
}
