package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChangeCodCallbackData {
    private BigDecimal detailCode;
    private String message;
}
