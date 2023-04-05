package com.imedia.service.postage.model;

import com.imedia.service.order.model.FilterOrderResponseFee;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CalculateFeeResponseAPI {
    private BigDecimal totalFee = BigDecimal.ZERO;
    private BigDecimal totalCod = BigDecimal.ZERO;
    private BigDecimal totalProductValue = BigDecimal.ZERO;
    private BigDecimal totalMoneyCollect = BigDecimal.ZERO;
    private CalculateFeeResponseDetailAPI fees;
    private List<FilterOrderResponseFee> feeDetails;

}
