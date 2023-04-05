package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CancelOrderRequest {
    private String orderCode;
    private Integer type;
    private Integer groupStatus;
    //Type 1 = order to
    //2 là order nhỏ
    //3 là hủy theo group status
}
