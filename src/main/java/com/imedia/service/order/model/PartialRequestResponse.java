package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PartialRequestResponse {
    private Long id;
    private BigDecimal orderDetailCode;
    private List<String> imgSuccess;
    private List<String> imgReturn;
    private BigDecimal partialCod;
    private BigDecimal expectCod;
}
