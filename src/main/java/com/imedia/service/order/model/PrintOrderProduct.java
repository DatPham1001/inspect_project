package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class PrintOrderProduct {
    private String name;
    private BigDecimal quantity;
    private String categoryName;
}
