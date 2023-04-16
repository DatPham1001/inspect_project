package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushKmOrderBaseResponse {
    private Integer code;
    private String message;
    private PushKmOrderResponse data;
}
