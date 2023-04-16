package com.imedia.service.postage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class ForWardConfigOM {
    private Integer id;
    private String code;
    private String carrierPackCode;
    private Integer priceSettingId;
    private String url;
    private String specialServices;
    private BigDecimal isHola;
}
