package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateSpecificFeeResponse {
    private Integer code;
    private String message;
    private CalculateSpecificFeeData data;
}
