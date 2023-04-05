package com.imedia.service.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterOrderResponseFee {
    private String feeName;
    private String feeCode;
    private BigDecimal feeValue = BigDecimal.ZERO;
}
