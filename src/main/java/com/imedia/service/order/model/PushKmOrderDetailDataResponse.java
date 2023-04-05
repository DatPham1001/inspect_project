package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PushKmOrderDetailDataResponse {
    private BigDecimal orderDetailCode;
    private Integer status;
    private String orderDetailShortId;
    private BigDecimal partnerDetailCode;
    private Integer shipId;
}
