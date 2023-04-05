package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderFileProduct {
    private String productName;
    private Integer quantity;
    private BigDecimal cod;
    private BigDecimal productValue;
    private Integer productCateId;

}
