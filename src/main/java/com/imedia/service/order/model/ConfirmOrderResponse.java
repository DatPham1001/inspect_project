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
public class ConfirmOrderResponse {
    private int totalRequest = 1;
    private int totalSuccess;
    private int totalFailed;
    private BigDecimal remainBalance;
    private BigDecimal minimumToConfirm;
    private String message;
}
