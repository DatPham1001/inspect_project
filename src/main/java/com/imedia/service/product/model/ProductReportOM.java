package com.imedia.service.product.model;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public interface ProductReportOM {
    Integer getId();

    BigDecimal getCod();

    BigDecimal getOrderDetailCode();

    String getName();

    Integer getProductCateId();

    String getCategoryName();

    Integer getQuantity();

    BigDecimal getValue();
}
