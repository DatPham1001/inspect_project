package com.imedia.service.order.model;

import com.imedia.service.postage.model.CalculateFeeReceivers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CacheChangeCodRequest {
    private BigDecimal oldCod;
    private BigDecimal newCod;
    private String packCode;
    private int counter;
    private CalculateFeeReceivers receivers;
}
