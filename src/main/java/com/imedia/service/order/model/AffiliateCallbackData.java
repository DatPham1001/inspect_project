package com.imedia.service.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AffiliateCallbackData {
    private long shopId;
    private String orderShortcode;
    private String shopOrderCode;
    private String carrierOrderCode;
    private long carrierId;
    private String carrierCode;
    private int status;
    private String statusText;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date actionTime;
    private String note;
    private int weight;
    private BigDecimal fee;
//    private BigDecimal feeCod;
//    private BigDecimal feeHandover;
//    private BigDecimal feePickup;
//    private BigDecimal feeInsurance;
//    private BigDecimal feeRefund;
//    private BigDecimal feeStorage;
//    private BigDecimal feeOther;
//    private BigDecimal feeDiscount;
//    private BigDecimal totalFee;
    private BigDecimal cod;
    private String callbackUrl;

}
