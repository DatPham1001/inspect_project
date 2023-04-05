package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FilterOrderDetailProductResponse {
    private Long id;
    private String name;
    private BigDecimal quantity;
    private BigDecimal value;
    private BigDecimal cod;
    private BigDecimal productCateId;
    private String categoryName;
}
