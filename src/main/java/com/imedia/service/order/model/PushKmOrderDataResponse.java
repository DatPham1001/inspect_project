package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PushKmOrderDataResponse {
    private BigDecimal orderCode;
    private Integer status;
    private String orderShortId;
    private BigDecimal partnerOrderCode;
    private Integer shipId;

}
