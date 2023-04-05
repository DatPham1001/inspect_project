package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Hls1FeeData {
    private BigDecimal TotalFee;
    private BigDecimal ServiceFee;
    private BigDecimal CoDFee;
    private BigDecimal DiscountFee;
    private BigDecimal InsuranceFee;
    private BigDecimal PlusFee;
}
