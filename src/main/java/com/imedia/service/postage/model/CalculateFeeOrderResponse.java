package com.imedia.service.postage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFeeOrderResponse {
    private Integer code;
    private String message;
    private CalculateFeeOrderData data;
}
