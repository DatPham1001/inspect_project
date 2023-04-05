package com.imedia.service.notify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class UpdateOrderPayload {
    private String detailCode;
    private BigDecimal shipperId;
}
