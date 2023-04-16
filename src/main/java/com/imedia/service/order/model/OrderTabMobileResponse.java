package com.imedia.service.order.model;

import com.imedia.service.order.enums.OrderStatusTabEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderTabMobileResponse {
    private int code;
    private String name;
    private int amount;

    public OrderTabMobileResponse(int code, int amount) {
        this.code = code;
        this.name = OrderStatusTabEnum.valueOf(code).message;
        this.amount = amount;
    }
}
