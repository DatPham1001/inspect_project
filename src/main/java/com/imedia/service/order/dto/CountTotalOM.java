package com.imedia.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountTotalOM {
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal totalFee= BigDecimal.ZERO;
    private BigDecimal totalOrder= BigDecimal.ZERO;
    private BigDecimal totalCod= BigDecimal.ZERO;
}
