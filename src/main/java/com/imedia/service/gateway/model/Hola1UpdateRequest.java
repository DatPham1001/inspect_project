package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Hola1UpdateRequest {
    private String code;
    private int weight;
    private int length;
    private int width;
    private int height;
    private String receiverAddress;
    private String receiverProvinceCode;
    private String receiverDistrictCode;
    private String receiverWardCode;
    private String receiver;
    private String receiverPhone;
    private BigDecimal cod;
}
