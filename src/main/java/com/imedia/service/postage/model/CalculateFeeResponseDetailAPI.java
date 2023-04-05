package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalculateFeeResponseDetailAPI {
    private BigDecimal transportFee = BigDecimal.ZERO;
    private BigDecimal insuranceFee = BigDecimal.ZERO;
    private BigDecimal codFee = BigDecimal.ZERO;
    private BigDecimal pickupFee = BigDecimal.ZERO;
    private BigDecimal porterFee = BigDecimal.ZERO;
    private BigDecimal partialFee = BigDecimal.ZERO;
    private BigDecimal handoverFee = BigDecimal.ZERO;
    private BigDecimal otherFee = BigDecimal.ZERO;

}
