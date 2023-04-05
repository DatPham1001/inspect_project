package com.imedia.service.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FilterOrderResponse {
    private BigDecimal svcOrderId;
    //    private Integer orderStatus;
    private Long totalAddressDelivery;
    private Long totalDistance;
    private Long totalOrderDetail;
    private Integer totalIsDoorDelivery;
    private Integer totalIsPartial;
    private Long type;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date utimestamp;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createAt;
    //Shop
    private Long shopId;
    private String shopOrderId;
    private String shopAddress;
    private String shopName;
    private String shopPhone;
    private String shopProvinceCode;
    private BigDecimal shopDistrictCode;
    private BigDecimal shopWardCode;
    private String shopProvince;
    private String shopDistrict;
    private String shopWard;
    //Shipper
    private String shipperName;
    private String shipperPhone;
    //pack
    private Integer servicePackId;
    private Integer servicePackSettingId;
    private String packName;
    private String packCode;
    private Integer paymentType;
    private Integer pickupType;
    private Integer orderStatus;
    private OrderStatusData statusMobile;
    //Fee
    private BigDecimal totalFee;
    private BigDecimal totalCod;
//    private BigDecimal realityCod;
    private List<FilterOrderResponseFee> orderFees;
    //OrderDetail
    private List<FilterOrderDetailResponse> orderDetails;
}
