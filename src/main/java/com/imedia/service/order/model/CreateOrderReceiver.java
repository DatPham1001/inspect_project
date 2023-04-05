package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateOrderReceiver {
    private BigDecimal orderDetailCode;
    private String name;
    private String phone;
    private String expectDate = "";
    private String expectTime = "";
    private Integer length = 10;
    private Integer width = 10;
    private Integer height = 10;
    private Integer weight;
    private Integer partialDelivery= 0;
    private Integer isFree= 1;
    private Integer confirmType;
    private Integer isRefund= 0;
    private Integer deliverShift = 0;
    private Integer requireNote = 0;
    private String note = "";
    private List<ExtraService> extraServices;
    private List<OrderDetailProduct> items;
}
