package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteOrderRequest {
    private String orderCodes;
    private Integer groupStatus;
}
