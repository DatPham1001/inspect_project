package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalSpecificFeeRequest {

    private String feeType;
    private String packCode;
    private String orderDetailCode;
    private int count;
    private String changedInfoType;
    private BigDecimal transportFee;
}
