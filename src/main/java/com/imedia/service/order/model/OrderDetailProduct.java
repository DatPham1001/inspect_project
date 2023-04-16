package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDetailProduct {
    private Integer id;
    private String productName = "";
    private Integer productCateId;
    private Integer category;
    private BigDecimal productValue = BigDecimal.ZERO;
    private BigDecimal quantity = BigDecimal.ONE;
    private BigDecimal cod = BigDecimal.ZERO;
}
