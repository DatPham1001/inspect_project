package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDraftRequest {
    private BigDecimal orderId;
    private Object orderDetail;
}
