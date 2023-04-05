package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PublicCalculateFeeResponse {
    private BigDecimal totalFee = BigDecimal.valueOf(20000);
}
