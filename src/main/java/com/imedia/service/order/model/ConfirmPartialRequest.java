package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConfirmPartialRequest {
    private BigDecimal orderDetailCode;
    private long partialRequestId;
    //1 là đồng ý 2 là từ chối
    private int confirmed;
}
