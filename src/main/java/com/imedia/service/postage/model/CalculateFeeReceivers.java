package com.imedia.service.postage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFeeReceivers implements Serializable {
    private BigDecimal orderDetailCode;
    private String name;
    private String phone;
    private String expectDate;
    private String expectTime;
    private String length;
    private String width;
    private String height;
    private String weight;
    private Integer partialDelivery;
    private Integer isFree;
    private String note;
    private String confirmType;
    private String isRefund;
    private String deliverShift;
    private String requireNote;
    private String isInsurance;
    //fee
    private Integer priceSettingId;
    //    private Integer packageId;
    private BigDecimal totalFee;
    private BigDecimal transportFee;
    private BigDecimal pickupFee;
    private BigDecimal porterFee;
    private BigDecimal partialFee;
    private BigDecimal handoverFee;
    private BigDecimal insuranceFee;
    private BigDecimal codFee;
    private BigDecimal otherFee;


}
