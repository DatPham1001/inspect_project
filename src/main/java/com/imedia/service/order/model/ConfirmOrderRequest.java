package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmOrderRequest {
    private String action;
    private String orderCodes;
}
