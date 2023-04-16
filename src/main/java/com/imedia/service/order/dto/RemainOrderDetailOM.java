package com.imedia.service.order.dto;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public interface RemainOrderDetailOM {
    BigDecimal getOrderCode();
    Integer getRemain();
}
