package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalculateSpecificFeeData {
    private String orderDetailCode;
    private String packCode;
    private String feeType;
    private BigDecimal fee;

}
